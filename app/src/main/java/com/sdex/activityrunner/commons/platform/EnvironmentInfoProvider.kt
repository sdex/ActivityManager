package com.sdex.activityrunner.commons.platform

interface EnvironmentInfoProvider {
    val isQuickSyncSupported: Boolean
    val bootCount: Int
}
