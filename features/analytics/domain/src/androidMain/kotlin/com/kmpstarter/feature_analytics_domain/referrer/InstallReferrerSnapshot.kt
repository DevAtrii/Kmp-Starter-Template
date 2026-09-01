package com.kmpstarter.feature_analytics_domain.referrer

internal data class InstallReferrerSnapshot(
    val installReferrer: String,
    val referrerClickTs: Long,
    val installBeginTs: Long,
    val referrerClickServerTs: Long,
    val installBeginServerTs: Long,
    val googlePlayInstant: Boolean,
    val installVersion: String,
)
