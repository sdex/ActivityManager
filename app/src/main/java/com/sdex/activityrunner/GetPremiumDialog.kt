package com.sdex.activityrunner

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog

class GetPremiumDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message = arguments!!.getInt(ARG_MESSAGE)
    return AlertDialog.Builder(activity!!)
      .setTitle(R.string.pro_version_dialog_title)
      .setMessage(message)
      .setPositiveButton(R.string.pro_version_get
      ) { _, _ -> PurchaseActivity.start(activity!!) }
      .create()
  }

  companion object {

    const val TAG = "GetPremiumDialog"

    private const val ARG_MESSAGE = "arg_message"

    fun newInstance(@StringRes messageRes: Int): GetPremiumDialog {
      val args = Bundle()
      args.putInt(ARG_MESSAGE, messageRes)
      val fragment = GetPremiumDialog()
      fragment.arguments = args
      return fragment
    }
  }
}
