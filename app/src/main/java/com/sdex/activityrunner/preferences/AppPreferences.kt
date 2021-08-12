package com.sdex.activityrunner.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var isNotExportedDialogShown: Boolean
        get() = preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, value)
            .apply()

    var isOreoBugWarningShown: Boolean
        get() = preferences.getBoolean(KEY_OREO_BUG_WARNING_SHOWN, false)
        set(value) = preferences.edit()
            .putBoolean(KEY_OREO_BUG_WARNING_SHOWN, value)
            .apply()

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

    val isRootIntegrationEnabled: Boolean
        get() = userPreferences.getBoolean(KEY_ROOT_INTEGRATION, false)

    @AppCompatDelegate.NightMode
    val theme: Int
        get() = userPreferences.getString(KEY_THEME, null)?.toInt()
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    companion object {

        private const val PREFERENCES_NAME = "ads_preferences"
        private const val KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown"
        private const val KEY_OREO_BUG_WARNING_SHOWN = "oreo_bug_warning_shown"

        /* advanced preferences */
        private const val KEY_SHOW_NOT_EXPORTED = "advanced_not_exported"
        private const val KEY_SHOW_SYSTEM_APP_LABEL = "advanced_system_app"
        const val KEY_ROOT_INTEGRATION = "advanced_root_integration"
        const val KEY_THEME = "appearance_theme"
    }
}
