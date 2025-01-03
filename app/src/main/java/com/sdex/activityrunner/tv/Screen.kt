package com.sdex.activityrunner.tv

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()
    @Serializable
    data class AppInfo(val packageName: String) : Screen()
}
