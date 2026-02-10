package com.sdex.activityrunner.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.tv.common.Screen

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    items: List<ApplicationModel>,
    navigateTo: (Screen) -> Unit,
    onRefresh: () -> Unit,
) {
    var showConfigDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 50.dp, horizontal = 100.dp),
        state = listState,
    ) {
        item(
            key = "header",
        ) {
            Header(
                onPreferencesClick = {
                    showConfigDialog = true
                },
            )
        }

        items(
            count = items.size,
            key = { index -> items[index].packageName },
        ) { index ->
            val item = items[index]
            ListItem(
                selected = false,
                onClick = {
                    navigateTo(Screen.AppInfo(item.packageName))
                },
                onLongClick = {
                    // TODO show options dialog:
                    //  launch, info, play store
                },
                headlineContent = { Text(text = item.name.toString()) },
                supportingContent = { Text(text = item.packageName) },
                trailingContent = { Text(text = item.activitiesCount.toString()) },
                leadingContent = {
                    AsyncImage(
                        model = if (LocalInspectionMode.current) {
                            R.drawable.ic_app_market
                        } else {
                            item.getPackageInfo(LocalContext.current)
                        },
                        contentDescription = item.name,
                        modifier = Modifier.size(dimensionResource(R.dimen.app_icon_size_tv)),
                    )
                },
            )
        }
    }

    if (showConfigDialog) {
        PreferencesDialog(
            modifier = Modifier.width(480.dp),
            showDialog = showConfigDialog,
            onConfigChanged = {
                onRefresh()
            },
            onDismissRequest = {
                showConfigDialog = false
            },
        )
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    onPreferencesClick: () -> Unit,
) {
    Column(
        modifier = modifier.height(120.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedIconButton(
                onClick = {
                    onPreferencesClick()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = "Preferences",
                )
            }

            OutlinedIconButton(
                onClick = {

                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                )
            }

            // TODO show total number of apps and activities
        }
    }
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun StartScreenPreview() {
    val fakeApps = listOf(
        ApplicationModel(
            packageName = "com.google.android.apps.maps",
            name = "Google Maps",
            activitiesCount = 12,
            exportedActivitiesCount = 4,
            system = false,
            enabled = true,
            versionCode = 12034500,
            versionName = "11.93.0",
            updateTime = System.currentTimeMillis(),
            installTime = System.currentTimeMillis() - 86400000,
            installerPackage = "com.android.vending",
        ),
        ApplicationModel(
            packageName = "com.android.chrome",
            name = "Chrome",
            activitiesCount = 8,
            exportedActivitiesCount = 2,
            system = false,
            enabled = true,
            versionCode = 500000000,
            versionName = "110.0.5481.65",
            updateTime = System.currentTimeMillis(),
            installTime = System.currentTimeMillis() - 172800000,
            installerPackage = "com.android.vending",
        ),
        ApplicationModel(
            packageName = "com.spotify.music",
            name = "Spotify",
            activitiesCount = 15,
            exportedActivitiesCount = 5,
            system = false,
            enabled = true,
            versionCode = 88140000,
            versionName = "8.8.14",
            updateTime = System.currentTimeMillis(),
            installTime = System.currentTimeMillis() - 259200000,
            installerPackage = "com.android.vending",
        ),
        ApplicationModel(
            packageName = "com.android.settings",
            name = "Settings",
            activitiesCount = 45,
            exportedActivitiesCount = 18,
            system = true,
            enabled = true,
            versionCode = 33,
            versionName = "13",
            updateTime = System.currentTimeMillis(),
            installTime = System.currentTimeMillis() - 31536000000,
            installerPackage = null,
        ),
        ApplicationModel(
            packageName = "com.whatsapp",
            name = "WhatsApp",
            activitiesCount = 10,
            exportedActivitiesCount = 3,
            system = false,
            enabled = true,
            versionCode = 222300,
            versionName = "2.23.3.76",
            updateTime = System.currentTimeMillis(),
            installTime = System.currentTimeMillis() - 604800000,
            installerPackage = "com.android.vending",
        ),
    )

    StartScreen(
        items = fakeApps,
        navigateTo = {},
        onRefresh = {},
    )
}
