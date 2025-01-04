package com.sdex.activityrunner.app.dialog.filter

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
import com.sdex.activityrunner.databinding.DialogFilterBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModels<FilterViewModel>()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { state ->
                    setState(state)
                    refresh()
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
    }

    private fun setState(state: FilterState) {
        when (state.sortBy) {
            ApplicationModel.Companion.NAME -> binding.sortByName.isChecked = true
            ApplicationModel.Companion.UPDATE_TIME -> binding.sortByUpdateTime.isChecked = true
            ApplicationModel.Companion.INSTALL_TIME -> binding.sortByInstallTime.isChecked = true
        }
        when (state.sortOrder) {
            GetApplicationsQuery.Companion.ASC -> binding.orderByAsc.isChecked = true
            GetApplicationsQuery.Companion.DESC -> binding.orderByDesc.isChecked = true
        }
        binding.showSystemApps.isChecked = state.isShowSystemApps
        binding.showSystemAppIndicator.isEnabled = state.isShowSystemApps
        binding.showSystemAppIndicator.isChecked = state.isShowSystemAppIndicator
        binding.showDisabledApps.isChecked = state.isShowDisabledApps
        binding.showDisabledAppIndicator.isEnabled = state.isShowDisabledApps
        binding.showDisabledAppIndicator.isChecked = state.isShowDisabledAppIndicator
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
