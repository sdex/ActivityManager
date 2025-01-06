package com.sdex.activityrunner.tv.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sdex.activityrunner.app.ActivitiesListViewModel
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.launchActivity
import com.sdex.activityrunner.tv.AppInfoScreen
import com.sdex.activityrunner.tv.StartScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()
    @Serializable
    data class AppInfo(val packageName: String) : Screen()
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
