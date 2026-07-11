package com.sdex.activityrunner.app.launcher

import android.content.Context

/**
 * Resolves the [LaunchStrategy] to use. Keeps the method-selection policy in one place: [get] for an
 * explicit method, [resolveAvailable] for the automatic "root, else Shizuku" choice.
 */
class LaunchStrategyFactory(
    private val strategies: Map<LaunchMethod, LaunchStrategy>,
) {

    fun get(method: LaunchMethod): LaunchStrategy =
        strategies.getValue(method)

    /**
     * Picks the mechanism to use for a non-exported activity: root if available, otherwise Shizuku.
     * Returns null when neither is usable (the caller should show onboarding).
     */
    suspend fun resolveAvailable(context: Context): LaunchStrategy? =
        ORDER.firstNotNullOfOrNull { method ->
            strategies[method]?.takeIf { it.isAvailable(context) }
        }

    private companion object {
        val ORDER = listOf(LaunchMethod.ROOT, LaunchMethod.SHIZUKU)
    }
}
