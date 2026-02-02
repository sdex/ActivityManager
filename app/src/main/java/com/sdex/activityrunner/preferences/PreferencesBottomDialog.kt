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
                viewModel.handleIntent(PreferencesIntent.SortByName)
            }
        }
        binding.sortByUpdateTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.handleIntent(PreferencesIntent.SortByUpdateTime)
            }
        }
        binding.sortByInstallTime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.handleIntent(PreferencesIntent.SortByInstallTime)
            }
        }
        binding.orderByAsc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.handleIntent(PreferencesIntent.SortOrderAsc)
            }
        }
        binding.orderByDesc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.handleIntent(PreferencesIntent.SortOrderDesc)
            }
        }
        binding.showSystemApps.setOnCheckedChangeListener { _, isChecked ->
            binding.showSystemAppIndicator.isEnabled = isChecked

            viewModel.handleIntent(PreferencesIntent.ToggleSystemApps(isChecked))
        }
        binding.showSystemAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            viewModel.handleIntent(PreferencesIntent.ToggleSystemAppIndicator(isChecked))
            update()
        }
        binding.showDisabledApps.setOnCheckedChangeListener { _, isChecked ->
            binding.showDisabledAppIndicator.isEnabled = isChecked

            viewModel.handleIntent(PreferencesIntent.ToggleDisabledApps(isChecked))
        }
        binding.showDisabledAppIndicator.setOnCheckedChangeListener { _, isChecked ->
            viewModel.handleIntent(PreferencesIntent.ToggleDisabledAppIndicator(isChecked))
            update()
        }
        binding.nonExported.setOnClickListener {
            binding.switchNonExported.isChecked = !binding.switchNonExported.isChecked
        }
        binding.switchNonExported.setOnCheckedChangeListener { _, isChecked ->
            viewModel.handleIntent(PreferencesIntent.ToggleNonExportedActivities(isChecked))
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
                viewModel.handleIntent(PreferencesIntent.ToggleTheme(themeId))
            }
        }
    }

    private fun setState(state: PreferencesState) {
        when (state.sortBy) {
            ApplicationModel.NAME -> binding.sortByName
            ApplicationModel.UPDATE_TIME -> binding.sortByUpdateTime
            ApplicationModel.INSTALL_TIME -> binding.sortByInstallTime
            else -> null
        }?.apply { isChecked = true }

        when (state.sortOrder) {
            GetApplicationsQuery.ASC -> binding.orderByAsc
            GetApplicationsQuery.DESC -> binding.orderByDesc
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
