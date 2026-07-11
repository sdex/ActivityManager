package com.sdex.activityrunner.app.launcher

import android.content.ComponentName
import android.content.Context

/**
 * A single mechanism for launching an activity component. Implementations encapsulate both the
 * availability check (is this mechanism usable on this device right now?) and the launch itself,
 * so the UI only has to pick a [LaunchMethod] and react to a [LaunchResult].
 */
interface LaunchStrategy {

    val method: LaunchMethod

    /**
     * Whether this mechanism can currently perform a launch: e.g. root is granted, the Shizuku
     * service is running and authorized, or WRITE_SECURE_SETTINGS has been granted. Runs off the
     * main thread (may do binder / process IO).
     */
    suspend fun isAvailable(context: Context): Boolean

    /** Attempts to launch [component]. Callers should check [isAvailable] first. */
    suspend fun launch(context: Context, component: ComponentName): LaunchResult
}
