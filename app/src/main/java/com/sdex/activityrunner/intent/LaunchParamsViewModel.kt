package com.sdex.activityrunner.intent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityLauncher
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.history.HistoryRepository
import com.sdex.activityrunner.di.IoDispatcher
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToShellCommandConverter
import com.sdex.activityrunner.intent.param.Action
import com.sdex.activityrunner.intent.param.MimeType
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.RootUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LaunchParamsViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val appPreferences: AppPreferences,
    private val activityLauncher: ActivityLauncher,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var isInitialized = false

    private val _launchParamsState = MutableStateFlow(LaunchParams())
    val launchParamsState: StateFlow<LaunchParams> = _launchParamsState

    private val _events = Channel<LaunchEvent>(Channel.BUFFERED)
    val events: Flow<LaunchEvent> = _events.receiveAsFlow()

    fun initialize(activityModel: ActivityModel?) {
        if (isInitialized) return

        _launchParamsState.update {
            it.copy(
                packageName = activityModel?.packageName,
                className = activityModel?.className,
            )
        }
        isInitialized = true
    }

    fun setLaunchParams(params: LaunchParams?) {
        _launchParamsState.value = params ?: LaunchParams()
    }

    fun setUseRoot(useRoot: Boolean) {
        _launchParamsState.update { it.copy(useRoot = useRoot) }
    }

    /**
     * Single launch entry point. Decides *how* to run the current [LaunchParams] — a normal
     * in-process launch, or `am start` as root when [LaunchParams.useRoot] is set — and reports the
     * outcome through [events] so the screen only has to render it.
     */
    fun launch(saveToHistory: Boolean) {
        val params = _launchParamsState.value
        if (saveToHistory) {
            addToHistory()
        }
        if (params.useRoot) {
            launchAsRoot(params)
        } else {
            val intent = LaunchParamsToIntentConverter(params).convert()
            val error = activityLauncher.launchIntent(intent)
            if (error != null) {
                _events.trySend(LaunchEvent.LaunchError(error))
            }
        }
    }

    private fun launchAsRoot(params: LaunchParams) {
        viewModelScope.launch {
            val event = withContext(ioDispatcher) {
                val suExecutable = appPreferences.suExecutable
                if (!RootUtils.isSuAvailable(suExecutable)) {
                    return@withContext LaunchEvent.RootUnavailable
                }
                val command = LaunchParamsToShellCommandConverter(params).convert()
                val output = RootUtils.execute(suExecutable, command)
                if (output != null && output.contains("Error")) {
                    LaunchEvent.RootError(output)
                } else {
                    LaunchEvent.RootSuccess
                }
            }
            _events.send(event)
        }
    }

    fun setValue(type: Int, value: String) {
        _launchParamsState.update {
            when (type) {
                R.string.launch_param_package_name -> it.copy(packageName = value)
                R.string.launch_param_class_name -> it.copy(className = value)
                R.string.launch_param_data -> it.copy(data = value)
                R.string.launch_param_action -> it.copy(action = value)
                R.string.launch_param_mime_type -> it.copy(mimeType = value)
                else -> it
            }
        }
    }

    fun setSingleSelection(type: Int, position: Int) {
        _launchParamsState.update {
            when (type) {
                R.string.launch_param_action -> {
                    it.copy(
                        action = if (position == 0) {
                            null
                        } else {
                            Action.getAction(Action.list()[position])
                        },
                    )
                }

                R.string.launch_param_mime_type -> {
                    it.copy(
                        mimeType = if (position == 0) {
                            null
                        } else {
                            MimeType.list()[position]
                        },
                    )
                }

                else -> it
            }
        }
    }

    fun setMultiSelection(type: Int, positions: ArrayList<Int>) {
        _launchParamsState.update {
            when (type) {
                R.string.launch_param_categories -> it.copy(categories = positions.toList())
                R.string.launch_param_flags -> it.copy(flags = positions.toList())
                else -> it
            }
        }
    }

    fun upsertExtra(extra: LaunchParamsExtra, position: Int) {
        _launchParamsState.update {
            val updatedExtras = it.extras.toMutableList()
            if (position == -1) {
                updatedExtras.add(extra)
            } else {
                updatedExtras[position] = extra
            }
            it.copy(extras = updatedExtras)
        }
    }

    fun removeExtra(position: Int) {
        _launchParamsState.update {
            val updatedExtras = it.extras.toMutableList()
            updatedExtras.removeAt(position)
            it.copy(extras = updatedExtras)
        }
    }

    fun getValueInitialValue(type: Int): String? {
        val launchParams = launchParamsState.value
        return when (type) {
            R.string.launch_param_package_name -> launchParams.packageName
            R.string.launch_param_class_name -> launchParams.className
            R.string.launch_param_data -> launchParams.data
            R.string.launch_param_action -> launchParams.action
            R.string.launch_param_mime_type -> launchParams.mimeType
            else -> throw IllegalStateException("Unknown type $type")
        }
    }

    fun getSingleSelectionInitialPosition(type: Int): Int {
        val launchParams = launchParamsState.value
        return when (type) {
            R.string.launch_param_action -> {
                if (launchParams.action == null) 0
                else Action.getActionKeyPosition(launchParams.action)
            }

            R.string.launch_param_mime_type -> {
                if (launchParams.mimeType == null) 0
                else MimeType.list().indexOf(launchParams.mimeType)
            }

            else -> throw IllegalStateException("Unknown type $type")
        }
    }

    fun getMultiSelectionInitialPositions(type: Int): ArrayList<Int> {
        val launchParams = launchParamsState.value
        return when (type) {
            R.string.launch_param_categories -> ArrayList(launchParams.categories)
            R.string.launch_param_flags -> ArrayList(launchParams.flags)
            else -> throw IllegalStateException("Unknown type $type")
        }
    }

    fun validateExtraInput(
        key: String,
        value: String,
        type: Int,
        isArray: Boolean,
    ): ExtraInputValidationResult {
        if (key.isEmpty()) {
            return ExtraInputValidationResult.KeyEmpty
        }

        if (value.isEmpty()) {
            return ExtraInputValidationResult.ValueEmpty
        }

        val values = if (isArray) value.split(",") else listOf(value)
        if (values.any { !isExtraFormatValid(type, it) }) {
            return ExtraInputValidationResult.InvalidType
        }

        return ExtraInputValidationResult.Valid
    }

    fun addToHistory() {
        viewModelScope.launch(ioDispatcher) {
            val historyConverter = LaunchParamsToHistoryConverter(launchParamsState.value)
            val historyModel = historyConverter.convert()
            historyRepository.insert(historyModel)
        }
    }

    private fun isExtraFormatValid(type: Int, value: String): Boolean {
        return try {
            when (type) {
                LaunchParamsExtraType.INT -> value.toInt()
                LaunchParamsExtraType.LONG -> value.toLong()
                LaunchParamsExtraType.FLOAT -> value.toFloat()
                LaunchParamsExtraType.DOUBLE -> value.toDouble()
            }
            true
        } catch (_: NumberFormatException) {
            false
        }
    }

    sealed class ExtraInputValidationResult {
        data object Valid : ExtraInputValidationResult()
        data object KeyEmpty : ExtraInputValidationResult()
        data object ValueEmpty : ExtraInputValidationResult()
        data object InvalidType : ExtraInputValidationResult()
    }

    sealed interface LaunchEvent {
        /** The in-process launch failed; [details] is the failure message to render. */
        data class LaunchError(val details: String?) : LaunchEvent

        /** Root launch requested but `su` is unavailable / not granted. */
        data object RootUnavailable : LaunchEvent

        /** The `am start` command completed without reporting an error. */
        data object RootSuccess : LaunchEvent

        /** The `am start` command failed; [details] is the captured shell output. */
        data class RootError(val details: String?) : LaunchEvent
    }
}
