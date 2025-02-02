package com.sdex.activityrunner.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val userPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var isNotExportedDialogShown: Boolean
        get() = preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false)
        set(value) = preferences.edit {
            putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, value)
        }
    var showDonate: Boolean
        get() = preferences.getBoolean(KEY_SHOW_DONATE, true)
        set(value) = preferences.edit {
            putBoolean(KEY_SHOW_DONATE, value)
        }
    val appOpenCounter: Int
        get() = preferences.getInt(KEY_OPEN_APP_COUNTER, 0)

    fun onAppOpened() {
        preferences.edit {
            putInt(KEY_OPEN_APP_COUNTER, appOpenCounter + 1)
        }
    }

    /* user preferences */

    var isShowSystemApps: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_SYSTEM_APPS, true)
        set(value) {
            userPreferences.edit {
                putBoolean(KEY_SHOW_SYSTEM_APPS, value)
            }
        }

    var isShowSystemAppIndicator: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_SYSTEM_APP_LABEL, false)
        set(value) {
            userPreferences.edit {
                putBoolean(KEY_SHOW_SYSTEM_APP_LABEL, value)
            }
        }

    var isShowDisabledApps: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_DISABLED_APPS, true)
        set(value) {
            userPreferences.edit {
                putBoolean(KEY_SHOW_DISABLED_APPS, value)
            }
        }

    var isShowDisabledAppIndicator: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_DISABLED_APP_LABEL, false)
        set(value) {
            userPreferences.edit {
                putBoolean(KEY_SHOW_DISABLED_APP_LABEL, value)
            }
        }

    var showNotExported: Boolean
        get() = userPreferences.getBoolean(KEY_SHOW_NOT_EXPORTED, false)
        set(value) {
            userPreferences.edit {
                putBoolean(KEY_SHOW_NOT_EXPORTED, value)
            }
        }

    var showLineNumbers: Boolean
        get() = preferences.getBoolean(KEY_SHOW_LINE_NUMBERS, true)
        set(value) = preferences.edit {
            putBoolean(KEY_SHOW_LINE_NUMBERS, value)
        }

    @AppCompatDelegate.NightMode
    var theme: Int
        get() = userPreferences.getString(KEY_THEME, null)?.toInt()
            ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        set(value) {
            userPreferences.edit {
                putString(KEY_THEME, value.toString())
            }
        }

    var sortBy: String
        get() = userPreferences.getString(KEY_SORT_BY, ApplicationModel.NAME)!!
        set(value) {
            userPreferences.edit {
                putString(KEY_SORT_BY, value)
            }
        }

    var sortOrder: String
        get() = userPreferences.getString(KEY_ORDER_BY, GetApplicationsQuery.ASC)!!
        set(value) {
            userPreferences.edit {
                putString(KEY_ORDER_BY, value)
            }
        }

    var suExecutable: String
        get() = userPreferences.getString(KEY_SU_EXECUTABLE, "su")!!
        set(value) {
            userPreferences.edit {
                putString(KEY_SU_EXECUTABLE, value)
            }
        }

    companion object {

        private const val PREFERENCES_NAME = "ads_preferences"
        private const val KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown"
        private const val KEY_SHOW_DONATE = "show_donate"
        private const val KEY_OPEN_APP_COUNTER = "open_app_counter"
        private const val KEY_SHOW_LINE_NUMBERS = "show_line_numbers"

        private const val KEY_SHOW_NOT_EXPORTED = "advanced_not_exported"
        const val KEY_SHOW_SYSTEM_APPS = "show_system_apps"
        private const val KEY_SHOW_SYSTEM_APP_LABEL = "advanced_system_app"
        const val KEY_SHOW_DISABLED_APPS = "show_disabled_apps"
        private const val KEY_SHOW_DISABLED_APP_LABEL = "advanced_disabled_app"
        const val KEY_THEME = "appearance_theme"

        const val KEY_SORT_BY = "sort_by"
        const val KEY_ORDER_BY = "order_by"
        const val KEY_SU_EXECUTABLE = "su_executable"
    }
}
