package com.sdex.activityrunner.tv.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sdex.activityrunner.app.ActivitiesListViewModel
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.launchActivity
import com.sdex.activityrunner.tv.AppInfoScreen
import com.sdex.activityrunner.tv.StartScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Main : Screen()
    @Serializable
    data class AppInfo(val packageName: String) : Screen()
}

@Composable
fun NavigationGraph() {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(Screen.Main)

    val viewModel = hiltViewModel<MainViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Screen.Main> {
                StartScreen(
                    items = state.items,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::search,
                    navigateTo = { backStack.add(it) },
                )
            }

            entry<Screen.AppInfo> { appInfo ->
                val viewModel = hiltViewModel<ActivitiesListViewModel>()
                AppInfoScreen(
                    viewModel = viewModel,
                    packageName = appInfo.packageName,
                    onItemClick = {
                        context.launchActivity(it)
                    },
                )
            }
        },
    )
}
