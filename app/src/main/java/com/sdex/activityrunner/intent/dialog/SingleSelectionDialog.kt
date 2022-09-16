package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.intent.dialog.source.ActionSource
import com.sdex.activityrunner.intent.dialog.source.MimeTypeSource

class SingleSelectionDialog : BaseDialogFragment() {

    private lateinit var callback: OnItemSelectedCallback

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val type: Int = requireArguments().getInt(ARG_TYPE)
        val initialPosition: Int = requireArguments().getInt(ARG_INITIAL_POSITION)

        val source = when (type) {
            R.string.launch_param_action -> ActionSource()
            R.string.launch_param_mime_type -> MimeTypeSource()
            else -> throw IllegalStateException("Wrong type: $type")
        }

        val list = source.list
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setSingleChoiceItems(
            list.toTypedArray(),
            initialPosition
        ) { _, which ->
            callback.onItemSelected(type, which)
            dismiss()
        }
        builder.setTitle(type)
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as OnItemSelectedCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnItemSelectedCallback")
        }
    }

    interface OnItemSelectedCallback {

        fun onItemSelected(type: Int, position: Int)
    }

    companion object {

        const val TAG = "SingleSelectionDialog"

        private const val ARG_TYPE = "arg_type"
        private const val ARG_INITIAL_POSITION = "arg_initial_position"

        fun newInstance(type: Int, initialPosition: Int): SingleSelectionDialog {
            val args = Bundle(2)
            args.putInt(ARG_TYPE, type)
            args.putInt(ARG_INITIAL_POSITION, initialPosition)
            val fragment = SingleSelectionDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
