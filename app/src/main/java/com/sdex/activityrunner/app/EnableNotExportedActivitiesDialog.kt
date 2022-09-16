package com.sdex.activityrunner.app

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.preferences.SettingsActivity

class EnableNotExportedActivitiesDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_enable_non_exported_title)
            .setMessage(R.string.dialog_enable_non_exported_message)
            .setPositiveButton(R.string.action_settings) { _, _ ->
                SettingsActivity.start(requireActivity())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        alertDialog.setCanceledOnTouchOutside(false)
        return alertDialog
    }

    companion object {

        const val TAG = "EnableNotExportedActivitiesDialog"
    }
}
