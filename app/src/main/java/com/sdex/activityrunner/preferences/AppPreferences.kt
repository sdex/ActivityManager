package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppPreferences(context: Context) {

  private val preferences: SharedPreferences
  private val userPreferences: SharedPreferences

  init {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    userPreferences = PreferenceManager.getDefaultSharedPreferences(context)
  }

  var isProVersion: Boolean
    get() = preferences.getBoolean(KEY_PRO, false)
    set(value) = preferences.edit()
      .putBoolean(KEY_PRO, value)
      .apply()

  var isHistoryWarningShown: Boolean
    get() = preferences.getBoolean(KEY_HISTORY_WARNING_SHOWN, false)
    set(value) = preferences.edit()
      .putBoolean(KEY_HISTORY_WARNING_SHOWN, value)
      .apply()

  var isNotExportedDialogShown: Boolean
    get() = preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false)
    set(value) = preferences.edit()
      .putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, value)
      .apply()

  var isPremiumDialogShown: Boolean
    get() = preferences.getBoolean(KEY_PREMIUM_DIALOG_SHOWN, false)
    set(value) = preferences.edit()
      .putBoolean(KEY_PREMIUM_DIALOG_SHOWN, value)
      .apply()

  var isOreoBugWarningShown: Boolean
    get() = preferences.getBoolean(KEY_OREO_BUG_WARNING_SHOWN, false)
    set(value) = preferences.edit()
      .putBoolean(KEY_OREO_BUG_WARNING_SHOWN, value)
      .apply()

  var appOpenCount: Int
    get() = preferences.getInt(KEY_APP_OPEN_COUNT, 0)
    set(value) {
      preferences.edit()
        .putInt(KEY_APP_OPEN_COUNT, value)
        .apply()
    }

  fun incrementAppOpenCount() {
    appOpenCount = appOpenCount.inc()
  }

  /* user preferences */

  val isShowSystemAppIndicator: Boolean
    get() = userPreferences.getBoolean(KEY_SHOW_SYSTEM_APP_LABEL,
      KEY_SHOW_SYSTEM_APP_LABEL_DEFAULT)

  var showNotExported: Boolean
    get() = userPreferences.getBoolean(KEY_SHOW_NOT_EXPORTED, KEY_SHOW_NOT_EXPORTED_DEFAULT)
    set(value) {
      userPreferences.edit()
        .putBoolean(KEY_SHOW_NOT_EXPORTED, value)
        .apply()
    }

  val isRootIntegrationEnabled: Boolean
    get() = userPreferences.getBoolean(KEY_ROOT_INTEGRATION, KEY_ROOT_INTEGRATION_DEFAULT)

  val getTheme: String?
    get() = userPreferences.getString(KEY_THEME, KEY_THEME_DEFAULT)

  val isBlackTheme: Boolean
    get() = userPreferences.getBoolean(KEY_THEME_BLACK, KEY_THEME_BLACK_DEFAULT)

  companion object {

    private const val PREFERENCES_NAME = "ads_preferences"
    private const val KEY_PRO = "pro"
    private const val KEY_HISTORY_WARNING_SHOWN = "history_warning_shown"
    private const val KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown"
    private const val KEY_PREMIUM_DIALOG_SHOWN = "premium_dialog_shown"
    private const val KEY_OREO_BUG_WARNING_SHOWN = "oreo_bug_warning_shown"
    private const val KEY_APP_OPEN_COUNT = "app_open_count"
    /* advanced preferences */
    private const val KEY_SHOW_NOT_EXPORTED = "advanced_not_exported"
    private const val KEY_SHOW_NOT_EXPORTED_DEFAULT = false
    const val KEY_ROOT_INTEGRATION = "advanced_root_integration"
    private const val KEY_ROOT_INTEGRATION_DEFAULT = false
    private const val KEY_SHOW_SYSTEM_APP_LABEL = "advanced_system_app"
    private const val KEY_SHOW_SYSTEM_APP_LABEL_DEFAULT = false
    const val KEY_THEME = "appearance_theme"
    private const val KEY_THEME_DEFAULT = "0"
    const val KEY_THEME_BLACK = "appearance_theme_black"
    private const val KEY_THEME_BLACK_DEFAULT = false
  }
}
