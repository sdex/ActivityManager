package com.sdex.activityrunner.tv

import android.app.UiModeManager
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.CardScale
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.PreferencesIntent
import com.sdex.activityrunner.preferences.PreferencesState
import com.sdex.activityrunner.preferences.PreferencesViewModel
import com.sdex.activityrunner.tv.common.FilterChipGroup
import com.sdex.activityrunner.tv.common.StandardDialog

private val sortByOptions = listOf(
    ApplicationModel.NAME to R.string.filter_sort_by_name,
    ApplicationModel.UPDATE_TIME to R.string.filter_sort_by_update_time,
    ApplicationModel.INSTALL_TIME to R.string.filter_sort_by_install_time,
)

private val sortOrderOptions = listOf(
    GetApplicationsQuery.ASC to R.string.filter_sort_order_asc,
    GetApplicationsQuery.DESC to R.string.filter_sort_order_desc,
)

@Composable
fun PreferencesDialog(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel = hiltViewModel(),
    showDialog: Boolean,
    onConfigChanged: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PreferencesContent(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        state = state,
        onHandleIntent = viewModel::handleIntent,
        onConfigChanged = onConfigChanged,
    )
}

@Composable
@OptIn(ExperimentalTvMaterial3Api::class)
private fun PreferencesContent(
    showDialog: Boolean,
    modifier: Modifier,
    state: PreferencesState,
    onHandleIntent: (PreferencesIntent) -> Unit,
    onDismissRequest: () -> Unit,
    onConfigChanged: () -> Unit,
) {
    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        text = {
            Column(
                modifier = modifier.wrapContentHeight(),
            ) {
                Text(text = stringResource(R.string.filter_header_sort_by))

                FilterChipGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    items = sortByOptions.map { stringResource(it.second) },
                    defaultSelectedItemIndex = sortByOptions.indexOfFirst {
                        it.first == state.sortBy
                    },
                    onSelectionChanged = {
                        when (sortByOptions[it].first) {
                            ApplicationModel.NAME ->
                                onHandleIntent(PreferencesIntent.SortByName)

                            ApplicationModel.UPDATE_TIME ->
                                onHandleIntent(PreferencesIntent.SortByUpdateTime)

                            ApplicationModel.INSTALL_TIME ->
                                onHandleIntent(PreferencesIntent.SortByInstallTime)
                        }
                        onConfigChanged()
                    },
                )

                FilterChipGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    items = sortOrderOptions.map { stringResource(it.second) },
                    defaultSelectedItemIndex = sortOrderOptions.indexOfFirst {
                        it.first == state.sortOrder
                    },
                    onSelectionChanged = {
                        when (sortOrderOptions[it].first) {
                            GetApplicationsQuery.ASC ->
                                onHandleIntent(PreferencesIntent.SortOrderAsc)

                            GetApplicationsQuery.DESC ->
                                onHandleIntent(PreferencesIntent.SortOrderDesc)
                        }
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
                            onHandleIntent(
                                PreferencesIntent.ToggleSystemApps(!state.isShowSystemApps),
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
                            onHandleIntent(
                                PreferencesIntent.ToggleSystemAppIndicator(!state.isShowSystemAppIndicator),
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
                            onHandleIntent(
                                PreferencesIntent.ToggleDisabledApps(!state.isShowDisabledApps),
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
                            onHandleIntent(
                                PreferencesIntent.ToggleDisabledAppIndicator(!state.isShowDisabledAppIndicator),
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

                SettingSwitchItem(
                    modifier = Modifier.padding(top = 16.dp),
                    checked = state.isShowNonExportedActivities,
                    onCheckedChange = {
                        onHandleIntent(
                            PreferencesIntent.ToggleNonExportedActivities(!state.isShowNonExportedActivities),
                        )
                        onConfigChanged()
                    },
                    title = R.string.pref_advanced_not_exported_title,
                    description = R.string.pref_advanced_not_exported_summary,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = ShapeDefaults.ExtraSmall,
    )
}

@Composable
@OptIn(ExperimentalTvMaterial3Api::class)
private fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @StringRes title: Int,
    @StringRes description: Int? = null,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        onClick = { if (enabled) onCheckedChange(!checked) },
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape(shape = MaterialTheme.shapes.small),
        scale = CardScale.None,
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val contentAlpha = if (enabled) 1.0f else 0.38f

                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    modifier = Modifier.alpha(contentAlpha),
                )
                if (description != null) {
                    Text(
                        text = stringResource(id = description),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(contentAlpha),
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=320")
@Composable
fun PreferencesContentPreviewAllEnabled() {
    PreferencesContent(
        showDialog = true,
        modifier = Modifier,
        state = PreferencesState(
            refresh = false,
            sortBy = ApplicationModel.NAME,
            sortOrder = GetApplicationsQuery.ASC,
            isShowSystemApps = true,
            isShowSystemAppIndicator = true,
            isShowDisabledApps = true,
            isShowDisabledAppIndicator = true,
            isShowNonExportedActivities = true,
            theme = UiModeManager.MODE_NIGHT_NO,
        ),
        onHandleIntent = {},
        onDismissRequest = {},
        onConfigChanged = {},
    )
}

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=320")
@Composable
fun PreferencesContentPreviewMinimalSettings() {
    PreferencesContent(
        showDialog = true,
        modifier = Modifier,
        state = PreferencesState(
            refresh = false,
            sortBy = ApplicationModel.UPDATE_TIME,
            sortOrder = GetApplicationsQuery.DESC,
            isShowSystemApps = false,
            isShowSystemAppIndicator = false,
            isShowDisabledApps = false,
            isShowDisabledAppIndicator = false,
            isShowNonExportedActivities = false,
            theme = UiModeManager.MODE_NIGHT_YES,
        ),
        onHandleIntent = {},
        onDismissRequest = {},
        onConfigChanged = {},
    )
}

@Preview(showBackground = true, device = Devices.TV_1080p)
@Composable
fun PreferencesContentPreviewSortByInstallTime() {
    PreferencesContent(
        showDialog = true,
        modifier = Modifier,
        state = PreferencesState(
            refresh = true,
            sortBy = ApplicationModel.INSTALL_TIME,
            sortOrder = GetApplicationsQuery.DESC,
            isShowSystemApps = true,
            isShowSystemAppIndicator = false,
            isShowDisabledApps = true,
            isShowDisabledAppIndicator = false,
            isShowNonExportedActivities = false,
            theme = UiModeManager.MODE_NIGHT_NO,
        ),
        onHandleIntent = {},
        onDismissRequest = {},
        onConfigChanged = {},
    )
}
