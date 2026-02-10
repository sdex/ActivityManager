package com.sdex.activityrunner.tv

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
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
    LaunchedEffect(Unit) {
        viewModel.getItems(packageName, null)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = UiData(null, emptyList()),
    )

    AppInfoContent(modifier, uiState, onItemClick)
}

@Composable
private fun AppInfoContent(
    modifier: Modifier,
    uiState: UiData,
    onItemClick: (ActivityModel) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 50.dp, horizontal = 100.dp),
        state = listState,
    ) {
        items(
            uiState.activities.size,
            key = { index -> uiState.activities[index].className },
        ) { index ->
            val item = uiState.activities[index]
            ListItem(
                selected = false,
                enabled = item.exported,
                onClick = { onItemClick(item) },
                headlineContent = { Text(text = item.name) },
                supportingContent = { Text(text = item.packageName) },
            )
        }
    }
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
        ),
        ActivityModel(
            name = "SettingsActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.SettingsActivity",
            label = "Settings",
            exported = true,
            enabled = true,
        ),
        ActivityModel(
            name = "AboutActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.AboutActivity",
            label = "About",
            exported = true,
            enabled = true,
        ),
        ActivityModel(
            name = "DebugActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.DebugActivity",
            label = "Debug",
            exported = false,
            enabled = false,
        ),
        ActivityModel(
            name = "TestActivity",
            packageName = "com.example.sampleapp",
            className = "com.example.sampleapp.TestActivity",
            label = "Test",
            exported = false,
            enabled = true,
        ),
    )

    val fakeUiData = UiData(
        application = fakeApp,
        activities = fakeActivities,
    )

    AppInfoContent(
        modifier = Modifier,
        uiState = fakeUiData,
        onItemClick = {},
    )
}
