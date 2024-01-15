package com.sdex.activityrunner.app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.MainActivity
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.DialogFilterBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        _binding = DialogFilterBinding.inflate(
            inflater.cloneInContext(contextThemeWrapper),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (appPreferences.sortBy) {
            ApplicationModel.NAME -> binding.sortByName.isChecked = true
            ApplicationModel.UPDATE_TIME -> binding.sortByUpdateTime.isChecked = true
            ApplicationModel.INSTALL_TIME -> binding.sortByInstallTime.isChecked = true
        }
        when (appPreferences.sortOrder) {
            GetApplicationsQuery.ASC -> binding.orderByAsc.isChecked = true
            GetApplicationsQuery.DESC -> binding.orderByDesc.isChecked = true
        }
        binding.showSystemApps.isChecked = appPreferences.isShowSystemApps
        binding.showSystemAppIndicator.isEnabled = appPreferences.isShowSystemApps
        binding.showSystemAppIndicator.isChecked = appPreferences.isShowSystemAppIndicator
        binding.showDisabledApps.isChecked = appPreferences.isShowDisabledApps
        binding.showDisabledAppIndicator.isEnabled = appPreferences.isShowDisabledApps
        binding.showDisabledAppIndicator.isChecked = appPreferences.isShowDisabledAppIndicator

        binding.sortByName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appPreferences.sortBy = ApplicationModel.NAME
                refresh()
            }
        }
        binding.sortByUpdateTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appPreferences.sortBy = ApplicationModel.UPDATE_TIME
                refresh()
            }
        }
        binding.sortByInstallTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appPreferences.sortBy = ApplicationModel.INSTALL_TIME
                refresh()
            }
        }
        binding.orderByAsc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appPreferences.sortOrder = GetApplicationsQuery.ASC
                refresh()
            }
        }
        binding.orderByDesc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                appPreferences.sortOrder = GetApplicationsQuery.DESC
                refresh()
            }
        }
        binding.showSystemApps.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isShowSystemApps = isChecked
            binding.showSystemAppIndicator.isEnabled = isChecked
            refresh()
        }
        binding.showSystemAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isShowSystemAppIndicator = isChecked
            update()
        }
        binding.showDisabledApps.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isShowDisabledApps = isChecked
            binding.showDisabledAppIndicator.isEnabled = isChecked
            refresh()
        }
        binding.showDisabledAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isShowDisabledAppIndicator = isChecked
            update()
        }
    }

    private fun refresh() {
        (activity as MainActivity?)?.refresh()
    }

    private fun update() {
        (activity as MainActivity?)?.update()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "FilterBottomSheetDialogFragment"
    }
}
