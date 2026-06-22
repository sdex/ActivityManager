package com.sdex.activityrunner.tv

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
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
                        .height(HeaderContainerHeight)
                        .padding(bottom = 28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    SearchField(
                        modifier = Modifier.fillMaxWidth(),
                        query = searchQuery.orEmpty(),
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
                Header(
                    appsCount = items.size,
                    onSearchClick = {
                        showSearchDialog = true
                    },
                    onPreferencesClick = {
                        showConfigDialog = true
                    },
                )
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
                    // TODO show options dialog:
                    //  launch, info, play store
                },
                headlineContent = { Text(text = item.name.toString()) },
                supportingContent = { Text(text = item.packageName) },
                trailingContent = { Text(text = item.activitiesCount.toString()) },
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

}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    appsCount: Int,
    onSearchClick: () -> Unit,
    onPreferencesClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderContainerHeight)
            .padding(bottom = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.filter_header, appsCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(
            modifier = Modifier.size(HeaderButtonSize),
            onClick = onSearchClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = stringResource(R.string.action_search),
                modifier = Modifier.size(HeaderIconSize),
            )
        }

        IconButton(
            modifier = Modifier.size(HeaderButtonSize),
            onClick = onPreferencesClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tune),
                contentDescription = stringResource(R.string.action_settings),
                modifier = Modifier.size(HeaderIconSize),
            )
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String?) -> Unit,
    onMoveFocusToList: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.DirectionDown) {
                    keyboardController?.hide()
                    onMoveFocusToList()
                    true
                } else {
                    false
                }
            }
            .border(width = 2.dp, color = borderColor, shape = shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = stringResource(R.string.action_search),
        )

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.action_search_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = { onQueryChange(it.takeIf(String::isNotEmpty)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
            )
        }

        if (query.isNotEmpty()) {
            IconButton(
                modifier = Modifier.size(SearchButtonSize),
                onClick = {
                    onQueryChange(null)
                    focusRequester.requestFocus()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Clear search",
                )
            }
        } else {
            Spacer(modifier = Modifier.size(SearchButtonSize))
        }
    }
}

private val HeaderButtonSize = 64.dp
private val HeaderIconSize = 32.dp
private val HeaderContainerHeight = 100.dp
private val SearchButtonSize = 56.dp

@Composable
private fun rememberApplicationIconModel(packageName: String): Any {
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

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun HeaderPreview() {
    Header(
        appsCount = 99,
        onSearchClick = {},
        onPreferencesClick = {},
    )
}

@Preview(showBackground = true, device = Devices.TV_720p)
@Composable
private fun SearchFieldPreview() {
    SearchField(
        modifier = Modifier.fillMaxWidth(),
        query = "Activity",
        onQueryChange = {},
        onMoveFocusToList = {},
    )
}
