package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.SparseBooleanArray
import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource
import java.util.*

class MultiSelectionDialog : DialogFragment() {

  private var callback: OnItemsSelectedCallback? = null
  private val selectedItems = SparseBooleanArray()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val type: Int = arguments!!.getInt(ARG_TYPE)
    val source: SelectionDialogSource? = arguments!!.getParcelable(ARG_SOURCE)
    val initialPositions: ArrayList<Int>? = arguments!!.getIntegerArrayList(ARG_INITIAL_POSITIONS)

    val list = source!!.list
    val checkedItems = BooleanArray(list.size)
    for (i in checkedItems.indices) {
      val checked = initialPositions!!.contains(i)
      checkedItems[i] = checked
      selectedItems.put(i, checked)
    }
    val builder = AlertDialog.Builder(activity!!)
    builder.setMultiChoiceItems(list.toTypedArray(), checkedItems
    ) { _, which, isChecked -> selectedItems.put(which, isChecked) }
    builder.setPositiveButton(android.R.string.ok) { _, _ ->
      val selectedPositions = ArrayList<Int>()
      for (i in 0 until selectedItems.size()) {
        val key = selectedItems.keyAt(i)
        val isSelected = selectedItems.get(key, false)
        if (isSelected) {
          selectedPositions.add(key)
        }
      }
      callback!!.onItemsSelected(type, selectedPositions)
    }
    builder.setTitle(type)
    return builder.create()
  }

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    try {
      callback = context as OnItemsSelectedCallback?
    } catch (e: ClassCastException) {
      throw ClassCastException(context!!.toString() + " must implement OnItemsSelectedCallback")
    }

  }

  interface OnItemsSelectedCallback {

    fun onItemsSelected(type: Int, positions: ArrayList<Int>)
  }

  companion object {

    const val TAG = "MultiSelectionDialog"

    private const val ARG_TYPE = "arg_type"
    private const val ARG_SOURCE = "arg_source"
    private const val ARG_INITIAL_POSITIONS = "arg_initial_positions"

    fun newInstance(type: Int, source: SelectionDialogSource,
                    initialPositions: ArrayList<Int>): MultiSelectionDialog {
      val args = Bundle(3)
      args.putInt(ARG_TYPE, type)
      args.putParcelable(ARG_SOURCE, source)
      args.putIntegerArrayList(ARG_INITIAL_POSITIONS, initialPositions)
      val fragment = MultiSelectionDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
