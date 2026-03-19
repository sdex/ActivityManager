package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.databinding.DialogInputValueBinding
import com.sdex.activityrunner.intent.LaunchParamsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ValueInputDialog : BaseDialogFragment() {

    private val viewModel: LaunchParamsViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val type = requireArguments().getInt(ARG_TYPE)
        val initialValue = requireArguments().getString(ARG_INITIAL_VALUE, "")

        val binding = DialogInputValueBinding.inflate(requireActivity().layoutInflater)

        binding.valueView.setText(initialValue)
        binding.valueView.setSelection(initialValue.length)
        binding.valueView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val newValue = binding.valueView.text.toString()
                viewModel.setValue(type, newValue)
                dismiss()
                return@setOnEditorActionListener true
            }
            false
        }
        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(type)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newValue = binding.valueView.text.toString()
                viewModel.setValue(type, newValue)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {

        const val TAG = "ValueInputDialog"

        private const val ARG_TYPE = "arg_type"
        private const val ARG_INITIAL_VALUE = "arg_initial_value"

        fun newInstance(type: Int, initialValue: String?): ValueInputDialog {
            return ValueInputDialog().apply {
                arguments = bundleOf(
                    ARG_TYPE to type,
                    ARG_INITIAL_VALUE to initialValue
                )
            }
        }
    }
}
