package com.sdex.activityrunner.onboarding

/** The two ordered setup steps for the Shizuku launch method. */
enum class OnboardingStep {
    INSTALL_SHIZUKU,
    GRANT_PERMISSION,
}

/** Per-step UI status. Only the [ACTIVE] step is actionable; later steps are [LOCKED]. */
enum class StepStatus {
    LOCKED,
    ACTIVE,
    COMPLETE,
}

/**
 * Immutable snapshot of the Shizuku setup. Completion is sequential: granting Shizuku access is only
 * reachable once the service is running. [writeSettingsGranted] is obtained automatically (via
 * Shizuku) once both steps are done, so it gates [isComplete] but is not a user-facing step.
 */
data class ShizukuOnboardingState(
    val shizukuInstalled: Boolean = false,
    val shizukuRunning: Boolean = false,
    val shizukuPermissionGranted: Boolean = false,
    val writeSettingsGranted: Boolean = false,
) {

    private val step1Complete: Boolean get() = shizukuRunning
    private val step2Complete: Boolean get() = step1Complete && shizukuPermissionGranted

    /** True once both visible steps are done (the write-settings grant may still be in flight). */
    val isShizukuReady: Boolean get() = step2Complete

    /** True once Shizuku is set up and the WRITE_SECURE_SETTINGS grant has gone through. */
    val isComplete: Boolean get() = step2Complete && writeSettingsGranted

    val currentStep: OnboardingStep
        get() = if (!step1Complete) OnboardingStep.INSTALL_SHIZUKU else OnboardingStep.GRANT_PERMISSION

    fun statusOf(step: OnboardingStep): StepStatus {
        val complete = when (step) {
            OnboardingStep.INSTALL_SHIZUKU -> step1Complete
            OnboardingStep.GRANT_PERMISSION -> step2Complete
        }
        return when {
            complete -> StepStatus.COMPLETE
            step == currentStep -> StepStatus.ACTIVE
            else -> StepStatus.LOCKED
        }
    }
}
