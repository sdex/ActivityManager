package com.sdex.activityrunner.preferences

import android.content.SharedPreferences

class AdvancedPreferences(private val sharedPreferences: SharedPreferences) {

  val isShowSystemAppIndicator: Boolean
    get() = sharedPreferences.getBoolean(KEY_SHOW_SYSTEM_APP_LABEL,
      KEY_SHOW_SYSTEM_APP_LABEL_DEFAULT)

  var showNotExported: Boolean
    get() = sharedPreferences.getBoolean(KEY_SHOW_NOT_EXPORTED, KEY_SHOW_NOT_EXPORTED_DEFAULT)
    set(show) {
      sharedPreferences.edit().putBoolean(KEY_SHOW_NOT_EXPORTED, show).apply()
    }

  val isRootIntegrationEnabled: Boolean
    get() = sharedPreferences.getBoolean(KEY_ROOT_INTEGRATION, KEY_ROOT_INTEGRATION_DEFAULT)

  val getTheme: String?
    get() = sharedPreferences.getString(KEY_THEME, KEY_THEME_DEFAULT)

  companion object {
    const val KEY_SHOW_NOT_EXPORTED = "advanced_not_exported"
    const val KEY_SHOW_NOT_EXPORTED_DEFAULT = false
    const val KEY_ROOT_INTEGRATION = "advanced_root_integration"
    const val KEY_ROOT_INTEGRATION_DEFAULT = false
    const val KEY_SHOW_SYSTEM_APP_LABEL = "advanced_system_app"
    const val KEY_SHOW_SYSTEM_APP_LABEL_DEFAULT = false
    const val KEY_THEME = "appearance_theme"
    const val KEY_THEME_DEFAULT = "0"
  }
}
