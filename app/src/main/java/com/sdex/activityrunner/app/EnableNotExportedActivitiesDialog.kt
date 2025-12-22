package com.sdex.activityrunner.app

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnableNotExportedActivitiesDialog : BaseDialogFragment() {

    private val viewModel by activityViewModels<ActivitiesListViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appPackageName = requireArguments().getString(ARG_PACKAGE_NAME)
            ?: throw IllegalArgumentException("No app package name provided")

        return MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.dialog_enable_non_exported_title)
            .setMessage(R.string.dialog_enable_non_exported_message)
            .setPositiveButton(R.string.activities_list_non_exported_show_always) { _, _ ->
                viewModel.showNotExported = true

                viewModel.reloadItems(appPackageName, showNotExported = true)
            }
            .setNegativeButton(R.string.activities_list_non_exported_show_once) { _, _ ->
                viewModel.reloadItems(appPackageName, showNotExported = true)
            }
            .create()
            .apply {
                setCanceledOnTouchOutside(true)
            }
    }

    companion object {

        private const val ARG_PACKAGE_NAME = "appPackageName"

        const val TAG = "EnableNotExportedActivitiesDialog"

        fun newInstance(appPackageName: String) = EnableNotExportedActivitiesDialog().apply {
            arguments = bundleOf(ARG_PACKAGE_NAME to appPackageName)
        }
    }
}
