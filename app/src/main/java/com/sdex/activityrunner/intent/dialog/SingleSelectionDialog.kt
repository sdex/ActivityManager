package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource

class SingleSelectionDialog : DialogFragment() {

  private var callback: OnItemSelectedCallback? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val type: Int = arguments!!.getInt(ARG_TYPE)
    val source: SelectionDialogSource? = arguments!!.getParcelable(ARG_SOURCE)
    val initialPosition: Int = arguments!!.getInt(ARG_INITIAL_POSITION)

    val list = source!!.list
    val builder = AlertDialog.Builder(activity!!)
    builder.setSingleChoiceItems(list.toTypedArray(),
      initialPosition) { _, which ->
      callback!!.onItemSelected(type, which)
      dismiss()
    }
    builder.setTitle(type)
    return builder.create()
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    try {
      callback = context as OnItemSelectedCallback?
    } catch (e: ClassCastException) {
      throw ClassCastException(context!!.toString() + " must implement OnItemSelectedCallback")
    }
  }

  interface OnItemSelectedCallback {

    fun onItemSelected(type: Int, position: Int)
  }

  companion object {

    const val TAG = "SingleSelectionDialog"

    private const val ARG_TYPE = "arg_type"
    private const val ARG_SOURCE = "arg_source"
    private const val ARG_INITIAL_POSITION = "arg_initial_position"

    fun newInstance(type: Int, source: SelectionDialogSource,
                    initialPosition: Int): SingleSelectionDialog {
      val args = Bundle(3)
      args.putInt(ARG_TYPE, type)
      args.putParcelable(ARG_SOURCE, source)
      args.putInt(ARG_INITIAL_POSITION, initialPosition)
      val fragment = SingleSelectionDialog()
      fragment.arguments = args
      return fragment
    }
  }
}