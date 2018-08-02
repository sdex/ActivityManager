package com.sdex.activityrunner.preferences

import android.content.SharedPreferences

class AdvancedPreferences(private val sharedPreferences: SharedPreferences) {

  val isShowSystemAppIndicator: Boolean
    get() = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_SYSTEM_APP,
      SettingsActivity.KEY_ADVANCED_SYSTEM_APP_DEFAULT)

  val isShowNotExported: Boolean
    get() = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_NOT_EXPORTED,
      SettingsActivity.KEY_ADVANCED_NOT_EXPORTED_DEFAULT)

  val isRootIntegrationEnabled: Boolean
    get() = sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_ROOT_INTEGRATION,
      SettingsActivity.KEY_ADVANCED_ROOT_INTEGRATION_DEFAULT)
}
