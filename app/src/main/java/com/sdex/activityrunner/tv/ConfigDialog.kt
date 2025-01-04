package com.sdex.activityrunner.tv

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Text
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.filter.FilterViewModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

private val sortByOptions = listOf<Pair<String, Int>>(
    ApplicationModel.Companion.NAME to R.string.filter_sort_by_name,
    ApplicationModel.Companion.UPDATE_TIME to R.string.filter_sort_by_update_time,
    ApplicationModel.Companion.INSTALL_TIME to R.string.filter_sort_by_install_time,
)

private val sortOrderOptions = listOf<Pair<String, Int>>(
    GetApplicationsQuery.Companion.ASC to R.string.filter_sort_order_asc,
    GetApplicationsQuery.Companion.DESC to R.string.filter_sort_order_desc,
)

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalTvMaterial3Api::class,
)
@Composable
fun ConfigDialog(
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel = hiltViewModel(),
    showDialog: Boolean,
    onConfigChanged: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
//            AccountsSectionDialogButton(
//                modifier = Modifier.padding(start = 8.dp),
//                text = stringResource(android.R.string.ok),
//                shouldRequestFocus = false,
//                onClick = onDismissRequest,
//            )
        },
        dismissButton = {
//            AccountsSectionDialogButton(
//                modifier = Modifier.padding(end = 8.dp),
//                text = stringResource(android.R.string.cancel),
//                shouldRequestFocus = false,
//                onClick = onDismissRequest,
//            )
        },
        title = {
        },
        text = {
            Column(
                modifier = modifier.wrapContentHeight(),
            ) {
                Text(text = stringResource(R.string.filter_header_sort_by))

                FilterChipGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    items = sortByOptions.map { stringResource(it.second) },
                    defaultSelectedItemIndex = sortByOptions.indexOfFirst { it.first == state.sortBy },
                    onSelectionChanged = {
                        viewModel.onSortByChanged(
                            sortByOptions[it].first,
                        )
                        onConfigChanged()
                    },
                )

                FilterChipGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    items = sortOrderOptions.map { stringResource(it.second) },
                    defaultSelectedItemIndex = sortOrderOptions.indexOfFirst { it.first == state.sortOrder },
                    onSelectionChanged = {
                        viewModel.onSortOrderChanged(
                            sortOrderOptions[it].first,
                        )
                        onConfigChanged()
                    },
                )

                Text(
                    text = stringResource(R.string.filter_system_apps_title),
                    modifier = Modifier.padding(top = 16.dp),
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = state.isShowSystemApps,
                        onClick = {
                            viewModel.onShowSystemAppsChanged(
                                !state.isShowSystemApps,
                            )
                            onConfigChanged()
                        },
                        content = { Text(stringResource(R.string.filter_toggle_show)) },
                        leadingIcon = {
                            if (state.isShowSystemApps) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Localized Description",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )

                    // not supported yet
                    /*FilterChip(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = state.isShowSystemAppIndicator,
                        enabled = state.isShowSystemApps,
                        onClick = {
                            viewModel.onShowSystemAppIndicatorChanged(
                                !state.isShowSystemAppIndicator,
                            )
                            onConfigChanged()
                        },
                        content = { Text(stringResource(R.string.filter_toggle_label)) },
                        leadingIcon = {
                            if (state.isShowSystemAppIndicator) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Localized Description",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )*/
                }

                Text(
                    text = stringResource(R.string.filter_disabled_apps_title),
                    modifier = Modifier.padding(top = 16.dp),
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = state.isShowDisabledApps,
                        onClick = {
                            viewModel.onShowDisabledAppsChanged(
                                !state.isShowDisabledApps,
                            )
                            onConfigChanged()
                        },
                        content = { Text(stringResource(R.string.filter_toggle_show)) },
                        leadingIcon = {
                            if (state.isShowDisabledApps) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Localized Description",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )

                    // not supported yet
                    /*FilterChip(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        selected = state.isShowDisabledAppIndicator,
                        enabled = state.isShowDisabledApps,
                        onClick = {
                            viewModel.onShowDisabledAppIndicatorChanged(
                                !state.isShowDisabledAppIndicator,
                            )
                            onConfigChanged()
                        },
                        content = { Text(stringResource(R.string.filter_toggle_label)) },
                        leadingIcon = {
                            if (state.isShowDisabledAppIndicator) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Localized Description",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        },
                    )*/
                }
            }
        },

        containerColor = MaterialTheme.colorScheme.surface,
        shape = ShapeDefaults.ExtraSmall,
    )
}
