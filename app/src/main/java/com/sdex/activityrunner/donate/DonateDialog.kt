package com.sdex.activityrunner.donate

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.commons.BaseDialogFragment
import com.sdex.commons.util.AppUtils

class DonateDialog : BaseDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireContext())
      .setTitle(R.string.about_donation)
      .setMessage(R.string.donate_message)
      .setPositiveButton(R.string.donate_action_text) { _, _ ->
        AppUtils.openLink(activity, "https://www.buymeacoffee.com/sdex")
      }
      .create()
  }

  companion object {

    const val TAG = "DonateDialog"

    fun newInstance(): DonateDialog {
      return DonateDialog()
    }
  }
}
