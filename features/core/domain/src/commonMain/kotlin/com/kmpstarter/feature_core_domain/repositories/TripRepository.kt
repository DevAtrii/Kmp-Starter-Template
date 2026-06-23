package com.kmpstarter.feature_core_domain.repositories

import com.kmpstarter.feature_core_domain.inference.InferredRecord
import com.kmpstarter.feature_core_domain.inference.RawGpsPoint

/**
 * 여행 동선 데이터 및 추론 결과의 영속화 처리를 관리하는 도메인 레포지토리 인터페이스입니다.
 */
interface TripRepository {

    /**
     * 수집된 Raw GPS 포인트들을 저장합니다.
     */
    suspend fun insertGpsPoints(points: List<RawGpsPoint>)

    /**
     * 특정 하루(dayId)에 수집된 Raw GPS 포인트 목록을 가져옵니다.
     */
    suspend fun getGpsPointsForDay(dayId: String): List<RawGpsPoint>

    /**
     * 특정 하루(dayId)에 대해 InferenceEngine을 실행하여 STAY / TRANSIT 동선을 추론한 후,
     * 이를 DB(records 테이블)에 영속화하고 추론 결과를 반환합니다.
     */
    suspend fun processAndSaveInference(tripId: String, dayId: String): List<InferredRecord>
}
