package com.sdex.activityrunner.app.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.databinding.DialogInputValueBinding
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RootConfigDialog : BaseDialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val initialValue = appPreferences.suExecutable

        val binding = DialogInputValueBinding.inflate(requireActivity().layoutInflater)

        binding.valueView.setText(initialValue)
        binding.valueView.setSelection(initialValue.length)
        binding.valueView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newValue = binding.valueView.text.toString()
                setValue(newValue)
                dismiss()
                return@setOnEditorActionListener true
            }
            false
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_root_config_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newValue = binding.valueView.text.toString()
                setValue(newValue)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    private fun setValue(value: String) {
        appPreferences.suExecutable = value
    }

    companion object {

        const val TAG = "RootConfigDialog"

        fun newInstance(): RootConfigDialog {
            return RootConfigDialog()
        }
    }
}
