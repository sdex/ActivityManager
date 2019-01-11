package com.sdex.activityrunner.premium

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sdex.commons.BaseDialogFragment

class PurchaseHelpDialog : BaseDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message = SpannableString(getString(com.sdex.activityrunner.R.string.pro_version_help_message))
    Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES)
    return AlertDialog.Builder(activity!!)
      .setTitle(com.sdex.activityrunner.R.string.pro_version_help_title)
      .setMessage(message)
      .setPositiveButton(android.R.string.ok, null)
      .create()
  }

  override fun onStart() {
    super.onStart()
    val messageView = dialog?.findViewById(android.R.id.message) as TextView?
    messageView?.movementMethod = LinkMovementMethod.getInstance()
  }

  companion object {

    const val TAG = "PurchaseHelpDialog"

    fun newInstance(): PurchaseHelpDialog {
      return PurchaseHelpDialog()
    }
  }
}
