package com.sdex.activityrunner.app.launcher

/**
 * The available mechanisms for launching a non-exported (or permission-guarded) activity.
 */
enum class LaunchMethod {
    /** `su -> am start`. Requires a rooted device. */
    ROOT,

    /**
     * Swaps the secure "assistant" setting to the target and triggers assist so the system launches
     * it. Requires Shizuku (to inject the assist key) and `WRITE_SECURE_SETTINGS` (for the swap).
     */
    SHIZUKU,
}
