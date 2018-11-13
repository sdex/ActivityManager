package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

  val preferences: SharedPreferences

  var isProVersion: Boolean
    get() = preferences.getBoolean(KEY_PRO, false)
    set(isPro) = preferences.edit()
      .putBoolean(KEY_PRO, isPro)
      .apply()

  var isHistoryWarningShown: Boolean
    get() = preferences.getBoolean(KEY_HISTORY_WARNING_SHOWN, false)
    set(historyWarningShown) = preferences.edit()
      .putBoolean(KEY_HISTORY_WARNING_SHOWN, historyWarningShown)
      .apply()

  var isNotExportedDialogShown: Boolean
    get() = preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false)
    set(notExportedDialogShown) = preferences.edit()
      .putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, notExportedDialogShown)
      .apply()

  init {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
  }

  companion object {

    private const val PREFERENCES_NAME = "ads_preferences"
    private const val KEY_PRO = "pro"
    private const val KEY_HISTORY_WARNING_SHOWN = "history_warning_shown"
    private const val KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown"
  }
}
