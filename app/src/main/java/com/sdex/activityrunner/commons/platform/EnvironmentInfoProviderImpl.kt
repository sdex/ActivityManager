package com.sdex.activityrunner.commons.platform

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

class EnvironmentInfoProviderImpl(
    private val context: Context,
) : EnvironmentInfoProvider {
    override val isQuickSyncSupported: Boolean
        get() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    override val bootCount: Int
        @RequiresApi(Build.VERSION_CODES.N)
        get() = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.BOOT_COUNT,
            0,
        )
}
