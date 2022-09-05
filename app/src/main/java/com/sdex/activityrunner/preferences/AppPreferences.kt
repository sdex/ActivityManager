package com.sdex.activityrunner.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var isNotExportedDialogShown: Boolean
        get() = preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, value)
            .apply()
    val appOpenCounter: Int
        get() = preferences.getInt(KEY_OPEN_APP_COUNTER, 0)

    fun onAppOpened() {
        preferences.edit {
            putInt(KEY_OPEN_APP_COUNTER, appOpenCounter + 1)
        }
    }

    /* user preferences */

    val isShowSystemAppIndicator: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_SYSTEM_APP_LABEL, false)

    var showNotExported: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_NOT_EXPORTED, false)
        set(value) {
            userPreferences.edit()
                .putBoolean(KEY_SHOW_NOT_EXPORTED, value)
                .apply()
        }

    @AppCompatDelegate.NightMode
    val theme: Int
        get() = userPreferences.getString(KEY_THEME, null)?.toInt()
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    companion object {

        private const val PREFERENCES_NAME = "ads_preferences"
        private const val KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown"
        private const val KEY_OPEN_APP_COUNTER = "open_app_counter"

        /* advanced preferences */
        private const val KEY_SHOW_NOT_EXPORTED = "advanced_not_exported"
        private const val KEY_SHOW_SYSTEM_APP_LABEL = "advanced_system_app"
        const val KEY_THEME = "appearance_theme"
    }
}
