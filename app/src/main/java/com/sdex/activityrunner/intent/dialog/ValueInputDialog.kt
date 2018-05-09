package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.EditorInfo
import com.sdex.activityrunner.R
import kotlinx.android.synthetic.main.dialog_input_value.view.*

class ValueInputDialog : DialogFragment() {

  private var callback: OnValueInputDialogCallback? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val type: Int = arguments!!.getInt(ARG_TYPE)
    val initialValue: String = arguments!!.getString(ARG_INITIAL_VALUE, "")

    val builder = AlertDialog.Builder(activity!!)
    val view = View.inflate(activity, R.layout.dialog_input_value, null)

    view.valueView.setText(initialValue)
    view.valueView.setSelection(initialValue.length)
    view.valueView.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        val newValue = view.valueView.text.toString()
        callback!!.onValueSet(type, newValue)
        dismiss()
        return@setOnEditorActionListener true
      }
      false
    }
    builder.setTitle(type)
      .setView(view)
      .setPositiveButton(android.R.string.ok) { _, _ ->
        val newValue = view.valueView.text.toString()
        callback!!.onValueSet(type, newValue)
      }
      .setNegativeButton(android.R.string.cancel, null)
    return builder.create()
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    try {
      callback = context as OnValueInputDialogCallback?
    } catch (e: ClassCastException) {
      throw ClassCastException(context!!.toString() + " must implement OnValueInputDialogCallback")
    }
  }

  interface OnValueInputDialogCallback {

    fun onValueSet(type: Int, value: String)
  }

  companion object {

    const val TAG = "ValueInputDialog"

    private const val ARG_TYPE = "arg_type"
    private const val ARG_INITIAL_VALUE = "arg_initial_value"

    fun newInstance(type: Int, initialValue: String?): ValueInputDialog {
      val args = Bundle(2)
      args.putInt(ARG_TYPE, type)
      args.putString(ARG_INITIAL_VALUE, initialValue)
      val fragment = ValueInputDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
