package com.kmpstarter.feature_core_data.repositories

import com.kmpstarter.feature_core_domain.inference.InferenceEngine
import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint
import com.kmpstarter.feature_core_domain.repositories.TripRepository
import com.kmpstarter.feature_database.dao.GpsPointDao
import com.kmpstarter.feature_database.dao.RecordDao
import com.kmpstarter.feature_database.entities.GpsPointEntity
import com.kmpstarter.feature_database.entities.RecordEntity
import kotlin.time.Clock
import kotlinx.coroutines.flow.first

/**
 * TripRepository의 실제 구현체로, Room Database와 InferenceEngine을 연결하여
 * GPS 원시 데이터를 관리하고 STAY / TRANSIT 동선을 추론 및 영속화합니다.
 */
class TripRepositoryImpl(
    private val gpsPointDao: GpsPointDao,
    private val recordDao: RecordDao,
    private val inferenceEngine: InferenceEngine
) : TripRepository {

    override suspend fun insertGpsPoints(points: List<RawGpsPoint>) {
        val entities = points.map { pt ->
            GpsPointEntity(
                timestamp = pt.timestamp,
                latitude = pt.latitude,
                longitude = pt.longitude,
                accuracy = pt.accuracy,
                speed = pt.speed,
                altitude = pt.altitude
            )
        }
        gpsPointDao.insertGpsPoints(entities)
    }

    override suspend fun getGpsPointsForDay(dayId: String): List<RawGpsPoint> {
        val entities = gpsPointDao.getGpsPointsForDay(dayId).first()
        return entities.map { entity ->
            RawGpsPoint(
                timestamp = entity.timestamp,
                latitude = entity.latitude,
                longitude = entity.longitude,
                accuracy = entity.accuracy,
                speed = entity.speed,
                altitude = entity.altitude
            )
        }
    }

    override suspend fun processAndSaveInference(tripId: String, dayId: String): List<InferredRecord> {
        // 1. 해당 날짜(dayId)의 원시 GPS 포인트들을 조회
        val rawPoints = getGpsPointsForDay(dayId)
        if (rawPoints.isEmpty()) return emptyList()

        // 2. DBSCAN 기반 추론 엔진을 통해 STAY와 TRANSIT 분리 실행
        val result = inferenceEngine.process(rawPoints)

        // 3. 중복 저장 방지를 위해, 해당 날짜의 기존 동선 기록(records)들을 소프트 삭제 처리
        val currentActiveRecords = recordDao.getActiveRecordsForDay(dayId).first()
        val now = Clock.System.now().toEpochMilliseconds()
        for (oldRecord in currentActiveRecords) {
            recordDao.softDeleteRecord(oldRecord.id, now)
        }

        // 4. 추론된 새 레코드들을 DB에 영속화하고 GPS 매핑 수행
        for (inferred in result.records) {
            val avgLat = if (inferred.type == "STAY" && inferred.gpsPoints.isNotEmpty()) {
                inferred.gpsPoints.map { it.latitude }.average()
            } else null

            val avgLng = if (inferred.type == "STAY" && inferred.gpsPoints.isNotEmpty()) {
                inferred.gpsPoints.map { it.longitude }.average()
            } else null

            val entity = RecordEntity(
                id = inferred.id,
                dayId = dayId,
                parentId = null,
                type = inferred.type,
                title = inferred.title,
                locationName = inferred.locationName,
                deletedAt = null,
                startTime = inferred.startTime,
                endTime = inferred.endTime,
                geoBounds = inferred.geoBounds,
                transportMode = inferred.transportMode,
                timestamp = if (inferred.type == "POINT") inferred.startTime else null,
                latitude = avgLat,
                longitude = avgLng
            )
            recordDao.insertRecord(entity)

            // 추론된 타임 범위에 속하는 Raw GPS 데이터들을 해당 dayId, tripId로 바인딩
            gpsPointDao.associateGpsPointsToDay(
                tripId = tripId,
                dayId = dayId,
                startTime = inferred.startTime,
                endTime = inferred.endTime
            )
        }

        return result.records
    }
}
