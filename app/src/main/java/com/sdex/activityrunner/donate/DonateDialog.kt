package com.sdex.activityrunner.donate

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.commons.BaseDialogFragment
import com.sdex.commons.util.AppUtils

class DonateDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.about_donation)
            .setMessage(R.string.donate_message)
            .setPositiveButton(R.string.donate_action_text) { _, _ ->
                AppUtils.openLink(requireActivity(), getString(R.string.donate_link))
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
