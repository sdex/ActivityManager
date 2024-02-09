package com.sdex.activityrunner.about

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DonateDialog : BaseDialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_about_support_development)
            .setTitle(R.string.about_donation)
            .setMessage(R.string.donate_message)
            .setNegativeButton(R.string.donate_negative_action_text) { _, _ ->
                appPreferences.showDonate = false
            }
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
