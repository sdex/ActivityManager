package com.sdex.activityrunner.onboarding

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShizukuOnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects the checker`() = runTest(dispatcher) {
        val checker = FakeChecker(installed = true, running = true)

        val viewModel = ShizukuOnboardingViewModel(checker)

        val state = viewModel.state.value
        assertThat(state.shizukuInstalled).isTrue()
        assertThat(state.shizukuRunning).isTrue()
        assertThat(state.shizukuPermissionGranted).isFalse()
        assertThat(state.currentStep).isEqualTo(OnboardingStep.GRANT_PERMISSION)
    }

    @Test
    fun `refresh recomputes state after an external change`() = runTest(dispatcher) {
        val checker = FakeChecker()
        val viewModel = ShizukuOnboardingViewModel(checker)
        assertThat(viewModel.state.value.currentStep).isEqualTo(OnboardingStep.INSTALL_SHIZUKU)

        checker.installed = true
        checker.running = true
        viewModel.refresh()

        assertThat(viewModel.state.value.currentStep).isEqualTo(OnboardingStep.GRANT_PERMISSION)
    }

    @Test
    fun `granting shizuku permission auto-grants write settings and completes`() =
        runTest(dispatcher) {
            val checker =
                FakeChecker(installed = true, running = true, permissionRequestGrants = true)
            val viewModel = ShizukuOnboardingViewModel(checker)

            viewModel.requestShizukuPermission()
            advanceUntilIdle()

            assertThat(checker.grantCount).isEqualTo(1)
            assertThat(viewModel.state.value.isComplete).isTrue()
        }

    @Test
    fun `auto-grants write settings on init when shizuku is already ready`() = runTest(dispatcher) {
        val checker = FakeChecker(installed = true, running = true, permissionGranted = true)

        val viewModel = ShizukuOnboardingViewModel(checker)
        advanceUntilIdle()

        assertThat(checker.grantCount).isEqualTo(1)
        assertThat(viewModel.state.value.writeSettingsGranted).isTrue()
        assertThat(viewModel.state.value.isComplete).isTrue()
    }

    @Test
    fun `failed auto-grant does not loop within a single cycle`() = runTest(dispatcher) {
        val checker = FakeChecker(
            installed = true,
            running = true,
            permissionGranted = true,
            grantSucceeds = false,
        )

        val viewModel = ShizukuOnboardingViewModel(checker)
        advanceUntilIdle()

        assertThat(checker.grantCount).isEqualTo(1)
        assertThat(viewModel.state.value.isComplete).isFalse()
    }

    @Test
    fun `failed auto-grant is retried on the next refresh`() = runTest(dispatcher) {
        val checker = FakeChecker(
            installed = true,
            running = true,
            permissionGranted = true,
            grantSucceeds = false,
        )
        val viewModel = ShizukuOnboardingViewModel(checker)
        advanceUntilIdle()
        assertThat(checker.grantCount).isEqualTo(1)

        viewModel.refresh()
        advanceUntilIdle()

        assertThat(checker.grantCount).isEqualTo(2)
    }

    @Test
    fun `successful grant completes even if the permission read is still stale`() =
        runTest(dispatcher) {
            // Grant succeeds but isWriteSecureSettingsGranted keeps returning false (cache not refreshed).
            val checker = FakeChecker(
                installed = true,
                running = true,
                permissionGranted = true,
                grantSucceeds = true,
                grantReflectsInReads = false,
            )

            val viewModel = ShizukuOnboardingViewModel(checker)
            advanceUntilIdle()

            assertThat(checker.grantCount).isEqualTo(1)
            assertThat(viewModel.state.value.isComplete).isTrue()
        }

    @Test
    fun `denying shizuku permission keeps the step active and does not grant`() =
        runTest(dispatcher) {
            val checker =
                FakeChecker(installed = true, running = true, permissionRequestGrants = false)
            val viewModel = ShizukuOnboardingViewModel(checker)

            viewModel.requestShizukuPermission()
            advanceUntilIdle()

            assertThat(checker.grantCount).isEqualTo(0)
            assertThat(viewModel.state.value.currentStep).isEqualTo(OnboardingStep.GRANT_PERMISSION)
        }

    private class FakeChecker(
        var installed: Boolean = false,
        var running: Boolean = false,
        var permissionGranted: Boolean = false,
        var writeSettingsGranted: Boolean = false,
        private val permissionRequestGrants: Boolean = true,
        private val grantSucceeds: Boolean = true,
        private val grantReflectsInReads: Boolean = true,
    ) : ShizukuSetupChecker {

        var grantCount = 0

        override fun isShizukuInstalled() = installed
        override fun isShizukuRunning() = running
        override fun isShizukuPermissionGranted() = permissionGranted
        override fun isWriteSecureSettingsGranted() = writeSettingsGranted

        override fun requestShizukuPermission(onResult: (granted: Boolean) -> Unit) {
            if (permissionRequestGrants) permissionGranted = true
            onResult(permissionRequestGrants)
        }

        override suspend fun grantWriteSecureSettings(): Boolean {
            grantCount++
            // grantReflectsInReads=false simulates a stale checkSelfPermission cache after pm grant.
            if (grantSucceeds && grantReflectsInReads) writeSettingsGranted = true
            return grantSucceeds
        }
    }
}
