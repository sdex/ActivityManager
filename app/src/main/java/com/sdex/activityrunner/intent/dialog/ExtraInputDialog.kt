package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.commons.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_input_extra.view.*

class ExtraInputDialog : BaseDialogFragment() {

  private lateinit var callback: OnKeyValueInputDialogCallback

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val initialExtra = arguments!!.getParcelable<LaunchParamsExtra>(ARG_INITIAL_EXTRA)
    val position = arguments!!.getInt(ARG_POSITION)

    val builder = AlertDialog.Builder(activity!!)
    val view = View.inflate(activity, R.layout.dialog_input_extra, null)

    if (initialExtra != null) {
      view.keyLayout.isHintAnimationEnabled = false
      view.valueLayout.isHintAnimationEnabled = false
      view.keyView.setText(initialExtra.key)
      view.valueView.setText(initialExtra.value)
      if (view.keyView.text != null) {
        view.keyView.setSelection(view.keyView.text!!.length)
      }
      view.keyLayout.isHintAnimationEnabled = true
      view.valueLayout.isHintAnimationEnabled = true
      setSelectedType(view, initialExtra.type)
    } else {
      view.rb_string.isChecked = true
    }
    builder.setTitle(R.string.dialog_add_extra_title)
      .setView(view)
      .setPositiveButton(android.R.string.ok, null)
      .setNegativeButton(android.R.string.cancel, null)
    val alertDialog = builder.create()
    alertDialog.setOnShowListener {
      alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        .setOnClickListener {
          val newKey = view.keyView.text.toString()
          val newValue = view.valueView.text.toString()
          val type = getSelectedType(view)

          view.keyLayout.error = null
          view.valueLayout.error = null

          if (newKey.isEmpty()) {
            view.keyLayout.error = getString(R.string.dialog_add_extra_key_empty)
            view.keyView.requestFocus()
            return@setOnClickListener
          }

          if (newValue.isEmpty()) {
            view.valueLayout.error = getString(R.string.dialog_add_extra_value_empty)
            view.valueView.requestFocus()
            return@setOnClickListener
          }

          if (!isExtraFormatValid(type, newValue)) {
            view.valueLayout.error = getString(R.string.dialog_add_extra_type_incorrect)
            return@setOnClickListener
          }

          val extra = LaunchParamsExtra(newKey, newValue, type, view.array.isChecked)
          callback.onValueSet(extra, position)
          dismiss()
        }
    }
    return alertDialog
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      callback = context as OnKeyValueInputDialogCallback
    } catch (e: ClassCastException) {
      throw ClassCastException("$context must implement OnKeyValueInputDialogCallback")
    }

  }

  private fun getSelectedType(view: View): Int {
    if (view.rb_string.isChecked) {
      return LaunchParamsExtraType.STRING
    }
    if (view.rb_int.isChecked) {
      return LaunchParamsExtraType.INT
    }
    if (view.rb_long.isChecked) {
      return LaunchParamsExtraType.LONG
    }
    if (view.rb_float.isChecked) {
      return LaunchParamsExtraType.FLOAT
    }
    if (view.rb_double.isChecked) {
      return LaunchParamsExtraType.DOUBLE
    }
    return if (view.rb_boolean.isChecked) {
      LaunchParamsExtraType.BOOLEAN
    } else -1
  }

  private fun setSelectedType(view: View, type: Int) {
    var radioButton: RadioButton? = null
    when (type) {
      LaunchParamsExtraType.STRING -> radioButton = view.rb_string
      LaunchParamsExtraType.INT -> radioButton = view.rb_int
      LaunchParamsExtraType.LONG -> radioButton = view.rb_long
      LaunchParamsExtraType.FLOAT -> radioButton = view.rb_float
      LaunchParamsExtraType.DOUBLE -> radioButton = view.rb_double
      LaunchParamsExtraType.BOOLEAN -> radioButton = view.rb_boolean
    }
    radioButton?.isChecked = true
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
      Log.d(TAG, "Failed to parse number")
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
      val args = Bundle(2)
      args.putParcelable(ARG_INITIAL_EXTRA, initialExtra)
      args.putInt(ARG_POSITION, position)
      val fragment = ExtraInputDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
