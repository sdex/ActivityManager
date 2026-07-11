package com.sdex.activityrunner.app.launcher

/**
 * Snapshot of the secure assistant settings taken before the [ShizukuLaunchStrategy] swap, so the
 * user's real assistant can be restored afterwards. A `null` field means the setting was unset.
 */
data class AssistantBackup(
    val assistant: String?,
    val voiceInteraction: String?,
)
