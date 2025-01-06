package com.sdex.activityrunner.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImage
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivitiesListViewModel
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.UiData
import com.sdex.activityrunner.app.launchActivity
import com.sdex.activityrunner.db.cache.ApplicationModel
import dagger.hilt.android.AndroidEntryPoint
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer

@AndroidEntryPoint
class TvActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val iconSize = resources.getDimensionPixelSize(R.dimen.app_icon_size_tv)
        Coil.setImageLoader(
            ImageLoader.Builder(this)
                .components {
                    add(AppIconKeyer())
                    add(AppIconFetcher.Factory(iconSize, false, this@TvActivity))
                }
                .build(),
        )

        setContent {
            ActivityManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                ) {
                    NavigationGraph()
                }
            }
        }
    }
}

@Composable
fun NavigationGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Main,
    ) {
        composable<Screen.Main> {
            val viewModel = hiltViewModel<MainViewModel>()
            val items = viewModel.items.asFlow().collectAsStateWithLifecycle(
                initialValue = emptyList(),
            )
            StartScreen(
                viewModel = viewModel,
                items = items.value,
                navigateTo = { navController.navigate(it) },
            )
        }
        composable<Screen.AppInfo> { backStackEntry ->
            val appInfo = backStackEntry.toRoute<Screen.AppInfo>()
            val viewModel = hiltViewModel<ActivitiesListViewModel>()
            AppInfoScreen(
                viewModel = viewModel,
                packageName = appInfo.packageName,
                onItemClick = {
                    context.launchActivity(it)
                },
            )
        }
    }
}

@Composable
fun AppInfoScreen(
    viewModel: ActivitiesListViewModel,
    packageName: String,
    onItemClick: (ActivityModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        viewModel.getItems(packageName, null)
    }

    val listState = rememberLazyListState()

    val uiState by viewModel.uiState.asFlow().collectAsStateWithLifecycle(
        initialValue = UiData(null, emptyList()),
    )

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
                headlineContent = { Text(text = item.name.toString()) },
                supportingContent = { Text(text = item.packageName) },
            )
        }
    }
}

@Composable
fun Header(
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO the header takes focus when navigate back
    Column(
        modifier = modifier.height(120.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedIconButton(
                onClick = {
                    onFilterClick()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = "Filters",
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

@Composable
fun StartScreen(
    viewModel: MainViewModel,
    items: List<ApplicationModel>,
    navigateTo: (Screen) -> Unit,
    modifier: Modifier = Modifier,
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
                onFilterClick = {
                    showConfigDialog = true
                },
            )
        }

        items(
            items.size,
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
                        model = item.getPackageInfo(LocalContext.current),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.app_icon_size_tv)),
                    )
                },
            )
        }
    }

    PreferencesDialog(
        modifier = Modifier.width(480.dp),
        showDialog = showConfigDialog,
        onConfigChanged = {
            viewModel.refresh()
        },
        onDismissRequest = {
            showConfigDialog = false
        },
    )
}
