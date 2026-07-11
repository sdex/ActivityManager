package com.sdex.activityrunner.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShizukuOnboardingViewModel @Inject constructor(
    private val checker: ShizukuSetupChecker,
) : ViewModel() {

    private val _state = MutableStateFlow(ShizukuOnboardingState())
    val state: StateFlow<ShizukuOnboardingState> = _state.asStateFlow()

    // Prevents a second grant while one is in flight; not a permanent latch, so a failed grant can be
    // retried on the next refresh() (e.g. screen resume).
    private var autoGranting = false

    init {
        refresh()
    }

    /** Re-reads all statuses. Call when the screen resumes (Shizuku may have changed externally). */
    fun refresh() {
        _state.value = readState()
        autoGrantWriteSettingsIfReady()
    }

    fun requestShizukuPermission() {
        checker.requestShizukuPermission { refresh() }
    }

    /** Once Shizuku is set up, grant WRITE_SECURE_SETTINGS automatically (not a user-facing step). */
    private fun autoGrantWriteSettingsIfReady() {
        val state = _state.value
        if (!state.isShizukuReady || state.writeSettingsGranted || autoGranting) return
        autoGranting = true
        viewModelScope.launch {
            val granted = checker.grantWriteSecureSettings()
            autoGranting = false
            if (granted) {
                // Trust the grant even if checkSelfPermission has not refreshed its cache yet.
                _state.value = _state.value.copy(writeSettingsGranted = true)
            }
            // On failure the state is left as-is; the next refresh() retries.
        }
    }

    private fun readState() = ShizukuOnboardingState(
        shizukuInstalled = checker.isShizukuInstalled(),
        shizukuRunning = checker.isShizukuRunning(),
        shizukuPermissionGranted = checker.isShizukuPermissionGranted(),
        writeSettingsGranted = checker.isWriteSecureSettingsGranted(),
    )
}
