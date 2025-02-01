package com.sdex.activityrunner.preferences

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdex.activityrunner.MainActivity
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.DialogPreferencesBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.extensions.createBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val THEME_LIGHT = 1
private const val THEME_DARK = 2
private const val THEME_AUTO = -1

@AndroidEntryPoint
class PreferencesBottomDialog : BottomSheetDialogFragment() {

    private val viewModel by viewModels<PreferencesViewModel>()

    private var _binding: DialogPreferencesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createBottomSheetDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme)
        _binding = DialogPreferencesBinding.inflate(
            inflater.cloneInContext(contextThemeWrapper),
            container,
            false,
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { state ->
                    setState(state)
                    if (state.refresh) {
                        refresh()
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.items.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { items ->
                    binding.header.text = getString(R.string.filter_header, items.size)
                }
        }

        binding.sortByName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortByChanged(ApplicationModel.Companion.NAME)
            }
        }
        binding.sortByUpdateTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortByChanged(ApplicationModel.Companion.UPDATE_TIME)
            }
        }
        binding.sortByInstallTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortByChanged(ApplicationModel.Companion.INSTALL_TIME)
            }
        }
        binding.orderByAsc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortOrderChanged(GetApplicationsQuery.Companion.ASC)
            }
        }
        binding.orderByDesc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.onSortOrderChanged(GetApplicationsQuery.Companion.DESC)
            }
        }
        binding.showSystemApps.setOnCheckedChangeListener { _, isChecked ->
            binding.showSystemAppIndicator.isEnabled = isChecked

            viewModel.onShowSystemAppsChanged(isChecked)
        }
        binding.showSystemAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onShowSystemAppIndicatorChanged(isChecked)
            update()
        }
        binding.showDisabledApps.setOnCheckedChangeListener { _, isChecked ->
            binding.showDisabledAppIndicator.isEnabled = isChecked

            viewModel.onShowDisabledAppsChanged(isChecked)
        }
        binding.showDisabledAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onShowDisabledAppIndicatorChanged(isChecked)
            update()
        }
        binding.nonExported.setOnClickListener {
            binding.switchNonExported.isChecked = !binding.switchNonExported.isChecked
        }
        binding.switchNonExported.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onShowNonExportedActivitiesChanged(isChecked)
        }

        val themeOptions = resources.getStringArray(R.array.pref_appearance_theme_list_titles)
        binding.themeAuto.text = themeOptions[0]
        binding.themeLight.text = themeOptions[1]
        binding.themeDark.text = themeOptions[2]

        binding.themeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val themeId = when (checkedId) {
                    R.id.themeLight -> THEME_LIGHT
                    R.id.themeDark -> THEME_DARK
                    else -> THEME_AUTO
                }
                viewModel.onThemeChanged(themeId)
            }
        }
    }

    private fun setState(state: PreferencesState) {
        when (state.sortBy) {
            ApplicationModel.Companion.NAME -> binding.sortByName
            ApplicationModel.Companion.UPDATE_TIME -> binding.sortByUpdateTime
            ApplicationModel.Companion.INSTALL_TIME -> binding.sortByInstallTime
            else -> null
        }?.apply { isChecked = true }

        when (state.sortOrder) {
            GetApplicationsQuery.Companion.ASC -> binding.orderByAsc
            GetApplicationsQuery.Companion.DESC -> binding.orderByDesc
            else -> null
        }?.apply { isChecked = true }

        binding.showSystemApps.isChecked = state.isShowSystemApps
        binding.showSystemAppIndicator.isEnabled = state.isShowSystemApps
        binding.showSystemAppIndicator.isChecked = state.isShowSystemAppIndicator
        binding.showDisabledApps.isChecked = state.isShowDisabledApps
        binding.showDisabledAppIndicator.isEnabled = state.isShowDisabledApps
        binding.showDisabledAppIndicator.isChecked = state.isShowDisabledAppIndicator
        binding.switchNonExported.isChecked = state.isShowNonExportedActivities

        when (state.theme) {
            THEME_LIGHT -> binding.themeLight
            THEME_DARK -> binding.themeDark
            else -> binding.themeAuto
        }.apply { isChecked = true }
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

        const val TAG = "PreferencesBottomDialog"
    }
}
