package com.sdex.activityrunner.app.launcher

import androidx.annotation.StringRes

/** Outcome of a [LaunchStrategy.launch] attempt. */
sealed interface LaunchResult {

    /** The activity was started successfully. */
    data object Success : LaunchResult

    /**
     * The mechanism is not usable right now (root/Shizuku unavailable, permission missing, ...).
     * The caller is expected to route the user to the matching onboarding screen.
     */
    data object Unavailable : LaunchResult

    /** The launch was attempted but failed. [messageRes] describes the failure to the user. */
    data class Error(@param:StringRes val messageRes: Int) : LaunchResult
}
