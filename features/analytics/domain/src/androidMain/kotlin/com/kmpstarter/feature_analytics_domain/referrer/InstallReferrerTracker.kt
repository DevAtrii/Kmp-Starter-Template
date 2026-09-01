package com.kmpstarter.feature_analytics_domain.referrer

import android.app.Application
import android.os.RemoteException
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse
import com.android.installreferrer.api.InstallReferrerStateListener
import com.kmpstarter.feature_analytics_domain.AppEvent
import com.kmpstarter.feature_analytics_domain.EventsTracker
import com.kmpstarter.feature_analytics_domain.setUserProperties
import com.kmpstarter.utils.datastore.AppDataStore
import com.kmpstarter.utils.datastore.booleanDataStore
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.mp.KoinPlatform
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

private data class InstallAttributionEvent(val props: Map<String, String>) : AppEvent(
    event = "install_attribution",
    properties = props
)

/**
 * One-shot Play Install Referrer capture.
 *
 * Talks to Play Store, parses the referrer, tracks `install_attribution`,
 * then writes UTM / click-id user properties. Persists a DataStore flag so
 * later launches skip the Play round-trip.
 *
 * Marks captured only after a successful track, or when Play reports
 * [InstallReferrerResponse.FEATURE_NOT_SUPPORTED]. Timeout / SERVICE_UNAVAILABLE
 * leave the flag unset → retry next cold start.
 */
internal class InstallReferrerTracker(
    private val eventsTracker: EventsTracker,
    appDataStore: AppDataStore,
) {
    companion object {
        private const val TAG = "InstallReferrerTracker"
        private val setupTimeout = 10.seconds

        fun create() = InstallReferrerTracker(
            eventsTracker = KoinPlatform.getKoin().get(),
            appDataStore = KoinPlatform.getKoin().get()
        )
    }

    private val captured = appDataStore.booleanDataStore(
        name = "_starter_install_referrer_captured",
        default = false,
    )

    suspend fun capture(app: Application) {
        if (captured.get() == true) return
        val client = InstallReferrerClient.newBuilder(app).build()
        try {
            val response = withTimeoutOrNull(setupTimeout) { awaitSetup(client) }
            if (response == null) {
                Log.i(TAG, "Play referrer setup timed out, will retry")
                return
            }
            when (response) {
                InstallReferrerResponse.OK -> onOk(client)
                InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    Log.i(TAG, "FEATURE_NOT_SUPPORTED")
                    captured.set(true)
                }

                else -> Log.i(TAG, "Play referrer unavailable, will retry")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "capture failed: ${e.message}", e)
        } finally {
            runCatching { client.endConnection() }
        }
    }

    private suspend fun onOk(client: InstallReferrerClient) {
        val details = try {
            client.installReferrer
        } catch (e: RemoteException) {
            Log.e(TAG, "getInstallReferrer: ${e.message}", e)
            return
        }
        val snapshot = InstallReferrerSnapshot(
            installReferrer = details.installReferrer.orEmpty(),
            referrerClickTs = details.referrerClickTimestampSeconds,
            installBeginTs = details.installBeginTimestampSeconds,
            referrerClickServerTs = details.referrerClickTimestampServerSeconds,
            installBeginServerTs = details.installBeginTimestampServerSeconds,
            googlePlayInstant = details.googlePlayInstantParam,
            installVersion = details.installVersion.orEmpty(),
        )
        val eventProps = InstallReferrerParser.eventProperties(snapshot)
        val userProps = InstallReferrerParser.userProperties(eventProps)
        val tracked = runCatching {
            eventsTracker.track(InstallAttributionEvent(eventProps))
        }.onFailure { Log.e(TAG, "track failed: ${it.message}", it) }.isSuccess
        if (!tracked) return
        runCatching { applyUserProperties(userProps) }
            .onFailure { Log.e(TAG, "user props failed: ${it.message}", it) }
        captured.set(true)
    }

    private suspend fun applyUserProperties(userProps: Map<String, String>) {
        if (userProps.isEmpty()) {
            Log.i(TAG, "User props are empty")
            return
        }
        eventsTracker.setUserProperties(values = userProps)
    }

    private suspend fun awaitSetup(client: InstallReferrerClient): Int =
        suspendCancellableCoroutine { cont ->
            client.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    if (cont.isActive) cont.resume(responseCode)
                }

                override fun onInstallReferrerServiceDisconnected() {
                    if (cont.isActive) {
                        cont.resume(InstallReferrerResponse.SERVICE_UNAVAILABLE)
                    }
                }
            })
        }
}
