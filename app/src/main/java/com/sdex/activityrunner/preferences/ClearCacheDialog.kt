package com.sdex.activityrunner.preferences

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClearCacheDialog : BaseDialogFragment() {

    private val viewModel by activityViewModels<SettingsViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_clear_cache_title)
            .setMessage(R.string.dialog_clear_cache_message)
            .setPositiveButton(R.string.dialog_clear_cache_action) { _, _ ->
                viewModel.clearCache()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {

        const val TAG = "ClearCacheDialog"

        fun newInstance() = ClearCacheDialog()
    }
}
