package com.sdex.activityrunner.intent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.dialog.source.CategoriesSource
import com.sdex.activityrunner.intent.dialog.source.FlagsSource
import com.sdex.commons.BaseDialogFragment
import java.util.*

class MultiSelectionDialog : BaseDialogFragment() {

  private var callback: OnItemsSelectedCallback? = null
  private val selectedItems = SparseBooleanArray()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val type: Int = arguments!!.getInt(ARG_TYPE)
    val initialPositions: ArrayList<Int>? = arguments!!.getIntegerArrayList(ARG_INITIAL_POSITIONS)

    val source = when (type) {
      R.string.launch_param_categories -> CategoriesSource()
      R.string.launch_param_flags -> FlagsSource()
      else -> {
        throw IllegalStateException("Wrong type: $type")
      }
    }

    val list = source.list
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

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      callback = context as OnItemsSelectedCallback?
    } catch (e: ClassCastException) {
      throw ClassCastException(context.toString() + " must implement OnItemsSelectedCallback")
    }

  }

  interface OnItemsSelectedCallback {

    fun onItemsSelected(type: Int, positions: ArrayList<Int>)
  }

  companion object {

    const val TAG = "MultiSelectionDialog"

    private const val ARG_TYPE = "arg_type"
    private const val ARG_INITIAL_POSITIONS = "arg_initial_positions"

    fun newInstance(type: Int, initialPositions: ArrayList<Int>): MultiSelectionDialog {
      val args = Bundle(2)
      args.putInt(ARG_TYPE, type)
      args.putIntegerArrayList(ARG_INITIAL_POSITIONS, initialPositions)
      val fragment = MultiSelectionDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
