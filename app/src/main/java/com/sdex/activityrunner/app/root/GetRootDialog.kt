package com.sdex.activityrunner.app.root

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.sdex.activityrunner.R
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.commons.BaseDialogFragment

class GetRootDialog : BaseDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(activity!!)
      .setTitle(R.string.pro_version_dialog_title)
      .setMessage(R.string.pro_version_unlock_root_integration)
      .setNeutralButton(R.string.root_check_compatibility)
      { _, _ -> CheckRootActivity.start(activity!!) }
      .setPositiveButton(R.string.pro_version_get)
      { _, _ -> PurchaseActivity.start(activity!!) }
      .create()
  }

  companion object {

    const val TAG = "GetRootDialog"

    fun newInstance(): GetRootDialog {
      return GetRootDialog()
    }
  }
}
