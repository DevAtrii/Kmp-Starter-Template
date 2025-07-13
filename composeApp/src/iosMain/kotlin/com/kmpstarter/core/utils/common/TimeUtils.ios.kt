package com.kmpstarter.core.utils.common

import platform.CoreFoundation.CFAbsoluteTimeGetCurrent
import platform.CoreFoundation.kCFAbsoluteTimeIntervalSince1970


actual fun epochMillis(): Long {
    val value = CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970
    return value.toLong()
}