package com.sdex.activityrunner.tv

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.tv.common.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    items: ImmutableList<ApplicationModel>,
    searchQuery: String? = null,
    onSearchQueryChange: (String?) -> Unit,
    navigateTo: (Screen) -> Unit,
) {
    var showConfigDialog by remember { mutableStateOf(false) }
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }
    var selectedOptionsItem by remember { mutableStateOf<ApplicationModel?>(null) }
    val listState = rememberLazyListState()
    val focusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }
    var focusedPackageName by rememberSaveable { mutableStateOf<String?>(null) }
    var restoreFocusRequest by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                restoreFocusRequest += 1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(restoreFocusRequest) {
        val packageName = focusedPackageName ?: return@LaunchedEffect
        delay(100.milliseconds)
        focusRequesters[packageName]?.requestFocus()
    }

    if (showSearchDialog) {
        BackHandler {
            onSearchQueryChange(null)
            showSearchDialog = false
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 48.dp, horizontal = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                        query = searchQuery.orEmpty(),
                        hint = stringResource(R.string.action_search_hint),
                        onQueryChange = onSearchQueryChange,
                        onMoveFocusToList = {
                            val packageName = items.firstOrNull()?.packageName
                            packageName?.let {
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
                    title = stringResource(R.string.app_name),
                    subtitle = stringResource(R.string.filter_header, items.size),
                ) {
                    TvHeaderIconButton(
                        icon = R.drawable.ic_search,
                        contentDescription = stringResource(R.string.action_search),
                        onClick = {
                            showSearchDialog = true
                        },
                    )

                    TvHeaderIconButton(
                        icon = R.drawable.ic_tune,
                        contentDescription = stringResource(R.string.action_settings),
                        onClick = {
                            showConfigDialog = true
                        },
                    )
                }
            }
        }

        items(
            count = items.size,
            key = { index -> items[index].packageName },
        ) { index ->
            val item = items[index]
            val focusRequester = remember {
                focusRequesters.getOrPut(item.packageName) {
                    FocusRequester()
                }
            }
            ListItem(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            focusedPackageName = item.packageName
                        }
                    },
                selected = false,
                onClick = {
                    focusedPackageName = item.packageName
                    navigateTo(Screen.AppInfo(item.packageName))
                },
                onLongClick = {
                    focusedPackageName = item.packageName
                    selectedOptionsItem = item
                },
                headlineContent = { Text(text = item.name.orEmpty()) },
                supportingContent = { Text(text = item.packageName) },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (item.pinnedAt != 0L) {
                            Icon(
                                painter = painterResource(R.drawable.ic_push_pin_filled_24),
                                contentDescription = stringResource(R.string.application_pinned),
                            )
                        }
                        Text(text = item.activitiesCount.toString())
                    }
                },
                leadingContent = {
                    AsyncImage(
                        model = rememberApplicationIconModel(item.packageName),
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
            },
            onDismissRequest = {
                showConfigDialog = false
            },
        )
    }

    selectedOptionsItem?.let { item ->
        TvApplicationOptionsDialog(
            modifier = Modifier.width(560.dp),
            item = item,
            onDismissRequest = {
                selectedOptionsItem = null
                restoreFocusRequest += 1
            },
        )
    }

}
@Composable
internal fun rememberApplicationIconModel(packageName: String): Any {
    if (LocalInspectionMode.current) {
        return R.drawable.ic_app_market
    }

    val context = LocalContext.current
    return remember(packageName) {
        context.getPackageInfoOrNull(packageName) ?: R.drawable.ic_app_market
    }
}

private fun Context.getPackageInfoOrNull(packageName: String): PackageInfo? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    } catch (_: Exception) {
        null
    }
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun StartScreenPreview() {
    val fakeApps = persistentListOf(
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
        searchQuery = "",
        onSearchQueryChange = {},
        navigateTo = {},
    )
}
