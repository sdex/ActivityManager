package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.databinding.DialogInputExtraBinding
import com.sdex.activityrunner.extensions.parcelable
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import timber.log.Timber

class ExtraInputDialog : BaseDialogFragment() {

    private lateinit var callback: OnKeyValueInputDialogCallback

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialExtra = requireArguments().parcelable<LaunchParamsExtra>(ARG_INITIAL_EXTRA)
        val position = requireArguments().getInt(ARG_POSITION)

        val binding = DialogInputExtraBinding.inflate(requireActivity().layoutInflater)

        if (initialExtra != null) {
            binding.keyLayout.isHintAnimationEnabled = false
            binding.valueLayout.isHintAnimationEnabled = false
            binding.keyView.setText(initialExtra.key)
            binding.valueView.setText(initialExtra.value)
            if (binding.keyView.text != null) {
                binding.keyView.setSelection(binding.keyView.text!!.length)
            }
            binding.keyLayout.isHintAnimationEnabled = true
            binding.valueLayout.isHintAnimationEnabled = true
            setSelectedType(binding, initialExtra.type)
        } else {
            binding.rbString.isChecked = true
        }

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_add_extra_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener {
                            val newKey = binding.keyView.text.toString()
                            val newValue = binding.valueView.text.toString()
                            val type = getSelectedType(binding)

                            binding.keyLayout.error = null
                            binding.valueLayout.error = null

                            if (newKey.isEmpty()) {
                                binding.keyLayout.error =
                                    getString(R.string.dialog_add_extra_key_empty)
                                binding.keyView.requestFocus()
                                return@setOnClickListener
                            }

                            if (newValue.isEmpty()) {
                                binding.valueLayout.error =
                                    getString(R.string.dialog_add_extra_value_empty)
                                binding.valueView.requestFocus()
                                return@setOnClickListener
                            }

                            if (!isExtraFormatValid(type, newValue)) {
                                binding.valueLayout.error =
                                    getString(R.string.dialog_add_extra_type_incorrect)
                                return@setOnClickListener
                            }

                            val extra =
                                LaunchParamsExtra(newKey, newValue, type, binding.array.isChecked)
                            callback.onValueSet(extra, position)
                            dismiss()
                        }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as OnKeyValueInputDialogCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnKeyValueInputDialogCallback")
        }
    }

    private fun getSelectedType(binding: DialogInputExtraBinding): Int {
        if (binding.rbString.isChecked) {
            return LaunchParamsExtraType.STRING
        }
        if (binding.rbInt.isChecked) {
            return LaunchParamsExtraType.INT
        }
        if (binding.rbLong.isChecked) {
            return LaunchParamsExtraType.LONG
        }
        if (binding.rbFloat.isChecked) {
            return LaunchParamsExtraType.FLOAT
        }
        if (binding.rbDouble.isChecked) {
            return LaunchParamsExtraType.DOUBLE
        }
        return if (binding.rbBoolean.isChecked) {
            LaunchParamsExtraType.BOOLEAN
        } else -1
    }

    private fun setSelectedType(binding: DialogInputExtraBinding, type: Int) {
        when (type) {
            LaunchParamsExtraType.STRING -> binding.rbString
            LaunchParamsExtraType.INT -> binding.rbInt
            LaunchParamsExtraType.LONG -> binding.rbLong
            LaunchParamsExtraType.FLOAT -> binding.rbFloat
            LaunchParamsExtraType.DOUBLE -> binding.rbDouble
            LaunchParamsExtraType.BOOLEAN -> binding.rbBoolean
            else -> null
        }?.isChecked = true
    }

    private fun isExtraFormatValid(type: Int, value: String): Boolean {
        try {
            when (type) {
                LaunchParamsExtraType.INT -> Integer.parseInt(value)
                LaunchParamsExtraType.LONG -> java.lang.Long.parseLong(value)
                LaunchParamsExtraType.FLOAT -> java.lang.Float.parseFloat(value)
                LaunchParamsExtraType.DOUBLE -> java.lang.Double.parseDouble(value)
            }
        } catch (e: NumberFormatException) {
            Timber.d("Failed to parse number")
            return false
        }

        return true
    }

    interface OnKeyValueInputDialogCallback {

        fun onValueSet(extra: LaunchParamsExtra, position: Int)
    }

    companion object {

        const val TAG = "ExtraInputDialog"

        private const val ARG_INITIAL_EXTRA = "arg_initial_extra"
        private const val ARG_POSITION = "arg_position"

        fun newInstance(initialExtra: LaunchParamsExtra?, position: Int): ExtraInputDialog {
            return ExtraInputDialog().apply {
                arguments = bundleOf(
                    ARG_INITIAL_EXTRA to initialExtra,
                    ARG_POSITION to position
                )
            }
        }
    }
}
