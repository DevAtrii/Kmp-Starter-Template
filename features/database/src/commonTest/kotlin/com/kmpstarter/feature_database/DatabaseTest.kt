package com.kmpstarter.feature_database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.kmpstarter.feature_database.entities.DayEntity
import com.kmpstarter.feature_database.entities.GpsPointEntity
import com.kmpstarter.feature_database.entities.RecordEntity
import com.kmpstarter.feature_database.entities.TripEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseTest {

    private lateinit var database: KmpStarterDatabase

    @BeforeTest
    fun setup() {
        // Use in-memory database for testing
        val builder = Room.inMemoryDatabaseBuilder<KmpStarterDatabase>()
        database = builder
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun testTripAndDayInsertAndRetrieve() = runTest {
        val tripDao = database.tripDao()

        val trip = TripEntity(
            id = "trip-1",
            title = "Test Seoul Trip",
            startDate = 1719176400000L, // 2024-06-24
            endDate = 1719435600000L     // 2024-06-27
        )

        tripDao.insertTrip(trip)

        val retrievedTrip = tripDao.getTripById("trip-1")
        assertNotNull(retrievedTrip)
        assertEquals("Test Seoul Trip", retrievedTrip.title)

        val day = DayEntity(
            id = "day-1",
            tripId = "trip-1",
            date = 1719176400000L,
            summary = "Day 1 Summary"
        )

        tripDao.insertDay(day)

        val retrievedDay = tripDao.getDayById("day-1")
        assertNotNull(retrievedDay)
        assertEquals("Day 1 Summary", retrievedDay.summary)

        // Soft delete test
        tripDao.softDeleteTrip("trip-1", 1719180000000L)
        val activeTrips = tripDao.getActiveTrips().first()
        assertEquals(0, activeTrips.size)
    }

    @Test
    fun testGpsPointInsertAndRetrieve() = runTest {
        val gpsPointDao = database.gpsPointDao()

        val point1 = GpsPointEntity(
            timestamp = 1719176400000L,
            latitude = 37.5665,
            longitude = 126.9780,
            accuracy = 5.0f,
            speed = 1.2f,
            altitude = 35.0
        )

        val point2 = GpsPointEntity(
            timestamp = 1719176410000L,
            latitude = 37.5666,
            longitude = 126.9781,
            accuracy = 4.5f,
            speed = 1.5f,
            altitude = 35.2
        )

        gpsPointDao.insertGpsPoints(listOf(point1, point2))

        val points = gpsPointDao.getGpsPointsInRange(1719176400000L, 1719176410000L)
        assertEquals(2, points.size)
        assertEquals(37.5665, points[0].latitude)
        assertEquals(37.5666, points[1].latitude)
    }

    @Test
    fun testRecordInsertAndRetrieve() = runTest {
        val tripDao = database.tripDao()
        val recordDao = database.recordDao()

        // Insert prerequisite Trip and Day
        val trip = TripEntity("trip-record-test", "Prereq Trip", 1719176400000L, 1719435600000L)
        val day = DayEntity("day-record-test", "trip-record-test", 1719176400000L)
        tripDao.insertTrip(trip)
        tripDao.insertDay(day)

        val stayRecord = RecordEntity(
            id = "record-stay-1",
            dayId = "day-record-test",
            type = "STAY",
            title = "Blue Bottle Coffee",
            locationName = "Seongsu-dong",
            startTime = 1719177000000L,
            endTime = 1719180600000L
        )

        recordDao.insertRecord(stayRecord)

        val retrieved = recordDao.getRecordById("record-stay-1")
        assertNotNull(retrieved)
        assertEquals("STAY", retrieved.type)
        assertEquals("Blue Bottle Coffee", retrieved.title)

        // Test Child Record (Hierarchy)
        val photoPin = RecordEntity(
            id = "record-photo-1",
            dayId = "day-record-test",
            parentId = "record-stay-1",
            type = "POINT",
            title = "Latte Latte",
            category = "PHOTO",
            source = "PHOTO_EXIF",
            timestamp = 1719178800000L,
            latitude = 37.5443,
            longitude = 127.0443
        )

        recordDao.insertRecord(photoPin)

        val children = recordDao.getChildRecords("record-stay-1")
        assertEquals(1, children.size)
        assertEquals("record-photo-1", children[0].id)
    }
}
