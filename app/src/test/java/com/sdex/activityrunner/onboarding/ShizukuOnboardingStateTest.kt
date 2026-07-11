package com.sdex.activityrunner.onboarding

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ShizukuOnboardingStateTest {

    @Test
    fun `nothing done - only install step active`() {
        val state = ShizukuOnboardingState()

        assertThat(state.currentStep).isEqualTo(OnboardingStep.INSTALL_SHIZUKU)
        assertThat(state.statusOf(OnboardingStep.INSTALL_SHIZUKU)).isEqualTo(StepStatus.ACTIVE)
        assertThat(state.statusOf(OnboardingStep.GRANT_PERMISSION)).isEqualTo(StepStatus.LOCKED)
        assertThat(state.isShizukuReady).isFalse()
        assertThat(state.isComplete).isFalse()
    }

    @Test
    fun `shizuku running - permission step becomes active`() {
        val state = ShizukuOnboardingState(shizukuRunning = true)

        assertThat(state.currentStep).isEqualTo(OnboardingStep.GRANT_PERMISSION)
        assertThat(state.statusOf(OnboardingStep.INSTALL_SHIZUKU)).isEqualTo(StepStatus.COMPLETE)
        assertThat(state.statusOf(OnboardingStep.GRANT_PERMISSION)).isEqualTo(StepStatus.ACTIVE)
        assertThat(state.isShizukuReady).isFalse()
    }

    @Test
    fun `both steps done but write settings pending - ready but not complete`() {
        val state = ShizukuOnboardingState(shizukuRunning = true, shizukuPermissionGranted = true)

        assertThat(state.statusOf(OnboardingStep.INSTALL_SHIZUKU)).isEqualTo(StepStatus.COMPLETE)
        assertThat(state.statusOf(OnboardingStep.GRANT_PERMISSION)).isEqualTo(StepStatus.COMPLETE)
        assertThat(state.isShizukuReady).isTrue()
        assertThat(state.isComplete).isFalse()
    }

    @Test
    fun `write settings granted after shizuku ready - complete`() {
        val state = ShizukuOnboardingState(
            shizukuInstalled = true,
            shizukuRunning = true,
            shizukuPermissionGranted = true,
            writeSettingsGranted = true,
        )

        assertThat(state.isShizukuReady).isTrue()
        assertThat(state.isComplete).isTrue()
    }

    @Test
    fun `write settings granted without shizuku running is not complete or ready`() {
        val state = ShizukuOnboardingState(writeSettingsGranted = true)

        assertThat(state.currentStep).isEqualTo(OnboardingStep.INSTALL_SHIZUKU)
        assertThat(state.isShizukuReady).isFalse()
        assertThat(state.isComplete).isFalse()
    }
}
