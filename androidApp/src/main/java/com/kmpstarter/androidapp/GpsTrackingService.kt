package com.kmpstarter.androidapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kmpstarter.feature_database.dao.GpsPointDao
import com.kmpstarter.feature_database.entities.GpsPointEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class GpsTrackingService : Service() {

    private val gpsPointDao: GpsPointDao by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    private var activityPendingIntent: PendingIntent? = null

    private var wakeLock: PowerManager.WakeLock? = null
    
    // Dynamic polling intervals based on user activity state
    private var currentInterval = 10000L // default 10s
    private var currentMinInterval = 5000L // default 5s
    private var isTrackingRunning = false

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "gps_tracking_channel"
        const val NOTIFICATION_ID = 9999
        const val ACTION_START = "ACTION_START_TRACKING"
        const val ACTION_STOP = "ACTION_STOP_TRACKING"
        
        const val ACTION_ACTIVITY_RECOGNITION = "com.kmpstarter.androidapp.ACTION_ACTIVITY_RECOGNITION"
        const val ACTIVITY_PENDING_INTENT_REQ_CODE = 4567
    }

    // Dynamic broadcast receiver to process detected activities
    private val activityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_ACTIVITY_RECOGNITION) {
                if (ActivityRecognitionResult.hasResult(intent)) {
                    val result = ActivityRecognitionResult.extractResult(intent)
                    result?.mostProbableActivity?.let { activity ->
                        handleDetectedActivity(activity)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        activityRecognitionClient = ActivityRecognition.getClient(this)
        
        setupWakeLock()
        createNotificationChannel()
        setupLocationCallback()
        registerActivityReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(activityReceiver)
        releaseWakeLock()
    }

    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "RAW::GpsTrackingWakeLock"
        ).apply {
            setReferenceCounted(false)
        }
    }

    private fun acquireWakeLock() {
        try {
            wakeLock?.acquire(24 * 60 * 60 * 1000L) // 24 hours max safety timeout
        } catch (e: Exception) {
            // Log wake lock acquisition errors
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            // Suppress release exceptions
        }
    }

    private fun registerActivityReceiver() {
        val filter = IntentFilter(ACTION_ACTIVITY_RECOGNITION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(activityReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(activityReceiver, filter)
        }
    }

    private fun startTracking() {
        if (isTrackingRunning) return
        isTrackingRunning = true
        
        acquireWakeLock()
        startForeground(NOTIFICATION_ID, createNotification("RAW 위치 수집 대기 중..."))
        requestLocationUpdates()
        requestActivityUpdates()
    }

    private fun stopTracking() {
        if (!isTrackingRunning) return
        isTrackingRunning = false

        removeActivityUpdates()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            // Suppress removal errors
        }
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    serviceScope.launch {
                        val entity = GpsPointEntity(
                            timestamp = System.currentTimeMillis(),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            speed = if (location.hasSpeed()) location.speed else null,
                            altitude = if (location.hasAltitude()) location.altitude else null
                        )
                        gpsPointDao.insertGpsPoint(entity)
                    }
                    val intervalDesc = if (currentInterval >= 30000L) "저전력 모드 (정지)" else "일반 모드 (이동)"
                    updateNotification("위치 수집 중 - $intervalDesc (${location.accuracy.toInt()}m)")
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, currentInterval
        ).apply {
            setMinUpdateIntervalMillis(currentMinInterval)
            setMaxUpdateDelayMillis(currentInterval + 5000L) // Allow batching to save battery
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            // Permissions lack
        }
    }

    private fun reRegisterLocationUpdates() {
        if (!isTrackingRunning) return
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            requestLocationUpdates()
        } catch (e: Exception) {
            // Suppress errors during dynamic interval changes
        }
    }

    private fun requestActivityUpdates() {
        val intent = Intent(ACTION_ACTIVITY_RECOGNITION).setPackage(packageName)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        activityPendingIntent = PendingIntent.getBroadcast(
            this,
            ACTIVITY_PENDING_INTENT_REQ_CODE,
            intent,
            flags
        )
        
        try {
            activityPendingIntent?.let { pendingIntent ->
                activityRecognitionClient.requestActivityUpdates(
                    30000L, // Check activity every 30 seconds
                    pendingIntent
                )
            }
        } catch (unlikely: SecurityException) {
            // Lack of ACTIVITY_RECOGNITION permission
        }
    }

    private fun removeActivityUpdates() {
        try {
            activityPendingIntent?.let { pendingIntent ->
                activityRecognitionClient.removeActivityUpdates(pendingIntent)
            }
        } catch (e: Exception) {
            // Suppress removal errors
        }
    }

    private fun handleDetectedActivity(activity: DetectedActivity) {
        // High confidence STILL state indicates the user is not moving
        val isStill = activity.type == DetectedActivity.STILL && activity.confidence >= 75
        val newInterval = if (isStill) 30000L else 10000L // 30s for STILL, 10s for MOVING
        val newMinInterval = if (isStill) 15000L else 5000L

        if (newInterval != currentInterval) {
            currentInterval = newInterval
            currentMinInterval = newMinInterval
            reRegisterLocationUpdates()
        }
    }

    private fun createNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("RAW 동선 트래킹")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(contentText))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "RAW GPS Tracking Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Continuous background location tracking for RAW trips"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
