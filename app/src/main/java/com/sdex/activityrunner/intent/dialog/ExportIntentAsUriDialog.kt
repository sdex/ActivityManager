package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.DialogExportIntentAsUriBinding
import com.sdex.activityrunner.intent.LaunchParams
import com.sdex.activityrunner.intent.converter.LaunchParamsToWebIntentConverter
import com.sdex.commons.BaseDialogFragment

class ExportIntentAsUriDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val launchParams = arguments?.getParcelable(ARG_LAUNCH_PARAMS) as LaunchParams?

        val binding = DialogExportIntentAsUriBinding.inflate(requireActivity().layoutInflater)

        val launchParamsToWebIntentConverter = LaunchParamsToWebIntentConverter(launchParams!!)
        val value = launchParamsToWebIntentConverter.convert()
        binding.value.text = value

        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.history_item_dialog_export_uri)
            .setView(binding.root)
            .setPositiveButton(R.string.dialog_export_intent_copy) { _, _ ->
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                        as ClipboardManager?
                val clip = ClipData.newPlainText("Intent URI", value)
                clipboard!!.setPrimaryClip(clip)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {

        const val TAG = "ExportIntentAsUriDialog"

        private const val ARG_LAUNCH_PARAMS = "arg_launch_params"

        fun newInstance(launchParams: LaunchParams): ExportIntentAsUriDialog {
            return ExportIntentAsUriDialog().apply {
                arguments = bundleOf(ARG_LAUNCH_PARAMS to launchParams)
            }
        }
    }
}
