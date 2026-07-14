package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.databinding.DialogExportIntentBinding
import com.sdex.activityrunner.extensions.parcelable
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.converter.LaunchParamsToShellCommandConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToWebIntentConverter

class ExportIntentDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val launchParams = requireArguments().parcelable<LaunchParams>(ARG_LAUNCH_PARAMS)!!
        val format = Format.valueOf(requireArguments().getString(ARG_FORMAT)!!)

        val binding = DialogExportIntentBinding.inflate(requireActivity().layoutInflater)

        val value = when (format) {
            Format.URI -> LaunchParamsToWebIntentConverter(launchParams).convert()
            Format.SHELL_COMMAND -> LaunchParamsToShellCommandConverter(launchParams).convert()
        }
        binding.value.text = value

        val unsupportedExtras = when (format) {
            Format.URI -> LaunchParamsToWebIntentConverter.getUnsupportedExtras(launchParams)
            Format.SHELL_COMMAND ->
                LaunchParamsToShellCommandConverter.getUnsupportedExtras(launchParams)
        }
        binding.warning.isVisible = unsupportedExtras.isNotEmpty()
        binding.warning.setText(format.warning)

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(format.title)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_export_intent_copy) { _, _ ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager?
                val clip = ClipData.newPlainText(format.clipLabel, value)
                clipboard?.setPrimaryClip(clip)
            }
            .create()
    }

    enum class Format(
        @StringRes val title: Int,
        @StringRes val warning: Int,
        val clipLabel: String,
    ) {
        URI(
            R.string.history_item_dialog_export_uri,
            R.string.dialog_export_intent_warning_array_extras,
            "Intent URI",
        ),
        SHELL_COMMAND(
            R.string.history_item_dialog_export_shell,
            R.string.dialog_export_intent_warning_boolean_array_extras,
            "Shell command",
        ),
    }

    companion object {

        const val TAG = "ExportIntentDialog"

        private const val ARG_LAUNCH_PARAMS = "arg_launch_params"
        private const val ARG_FORMAT = "arg_format"

        fun newInstance(launchParams: LaunchParams, format: Format): ExportIntentDialog {
            return ExportIntentDialog().apply {
                arguments = bundleOf(
                    ARG_LAUNCH_PARAMS to launchParams,
                    ARG_FORMAT to format.name,
                )
            }
        }
    }
}
