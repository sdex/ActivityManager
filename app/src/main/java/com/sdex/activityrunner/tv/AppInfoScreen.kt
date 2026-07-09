package com.sdex.activityrunner.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivitiesListViewModel
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.app.UiData
import com.sdex.activityrunner.db.cache.ApplicationModel

@Composable
fun AppInfoScreen(
    viewModel: ActivitiesListViewModel,
    packageName: String,
    modifier: Modifier = Modifier,
    onItemClick: (ActivityModel) -> Unit,
) {
    LaunchedEffect(packageName) {
        viewModel.getItems(packageName, null)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppInfoContent(
        modifier = modifier,
        packageName = packageName,
        uiState = uiState,
        onSearchQueryChange = viewModel::filterItems,
        onItemClick = onItemClick,
    )
}

@Composable
private fun AppInfoContent(
    modifier: Modifier,
    packageName: String,
    uiState: UiData,
    onSearchQueryChange: (String?) -> Unit,
    onItemClick: (ActivityModel) -> Unit,
) {
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val focusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }

    if (showSearchDialog) {
        BackHandler {
            onSearchQueryChange(null)
            showSearchDialog = false
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 48.dp, horizontal = 96.dp),
        state = listState,
    ) {
        if (showSearchDialog) {
            item(
                key = "search",
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HeaderContainerHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    TvSearchField(
                        modifier = Modifier.fillMaxWidth(),
                        query = uiState.searchText.orEmpty(),
                        hint = stringResource(R.string.action_search_activity_hint),
                        onQueryChange = onSearchQueryChange,
                        onMoveFocusToList = {
                            val className = uiState.activities.firstOrNull()?.className
                            className?.let {
                                focusRequesters[it]?.requestFocus()
                            }
                        },
                    )
                }
            }
        } else {
            item(
                key = "header",
            ) {
                TvListHeader(
                    title = uiState.application?.name ?: packageName,
                    subtitle = activityCountText(uiState),
                    leadingContent = {
                        AsyncImage(
                            model = rememberApplicationIconModel(packageName),
                            contentDescription = uiState.application?.name,
                            modifier = Modifier.size(
                                dimensionResource(R.dimen.app_icon_size_tv),
                            ),
                        )
                    },
                ) {
                    TvHeaderIconButton(
                        icon = R.drawable.ic_search,
                        contentDescription = stringResource(R.string.action_search),
                        onClick = {
                            showSearchDialog = true
                        },
                    )
                }
            }
        }

        items(
            uiState.activities.size,
            key = { index -> uiState.activities[index].className },
        ) { index ->
            val item = uiState.activities[index]
            val focusRequester = remember {
                focusRequesters.getOrPut(item.className) {
                    FocusRequester()
                }
            }
            ListItem(
                modifier = Modifier
                    .focusRequester(focusRequester),
                selected = false,
                enabled = !item.launchRequiresRoot,
                onClick = { onItemClick(item) },
                headlineContent = { Text(text = item.name) },
                supportingContent = { Text(text = item.packageName) },
            )
        }
    }
}

@Composable
private fun activityCountText(
    uiState: UiData,
): String {
    val context = LocalContext.current
    val totalActivitiesCount = uiState.application?.activitiesCount ?: uiState.allActivities.size
    val exportedActivitiesCount = uiState.application?.exportedActivitiesCount
        ?: uiState.allActivities.count { it.exported }
    val totalActivitiesFormattedText = context.resources.getQuantityString(
        R.plurals.activities_count,
        totalActivitiesCount,
        totalActivitiesCount,
    )

    return stringResource(
        R.string.app_info_activities_number,
        totalActivitiesFormattedText,
        exportedActivitiesCount,
    )
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun AppInfoContentPreview() {
    val fakeApp = ApplicationModel(
        packageName = "com.example.sampleapp",
        name = "Sample App",
        activitiesCount = 5,
        exportedActivitiesCount = 3,
        system = false,
        enabled = true,
        versionCode = 100L,
        versionName = "1.0.0",
        updateTime = System.currentTimeMillis(),
        installTime = System.currentTimeMillis(),
        installerPackage = "com.android.vending",
    )

    val fakeActivities = listOf(
        ActivityModel(
            name = "MainActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.MainActivity",
            label = "Main",
            exported = true,
            enabled = true,
            permission = null,
        ),
        ActivityModel(
            name = "SettingsActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.SettingsActivity",
            label = "Settings",
            exported = true,
            enabled = true,
            permission = null,
        ),
        ActivityModel(
            name = "AboutActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.AboutActivity",
            label = "About",
            exported = true,
            enabled = true,
            permission = "permission",
        ),
        ActivityModel(
            name = "DebugActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.DebugActivity",
            label = "Debug",
            exported = false,
            enabled = false,
            permission = null,
        ),
        ActivityModel(
            name = "TestActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.TestActivity",
            label = "Test",
            exported = false,
            enabled = true,
            permission = null,
        ),
    )

    val fakeUiData = UiData(
        application = fakeApp,
        activities = fakeActivities,
    )

    AppInfoContent(
        modifier = Modifier,
        packageName = fakeApp.packageName,
        uiState = fakeUiData,
        onSearchQueryChange = {},
        onItemClick = {},
    )
}
