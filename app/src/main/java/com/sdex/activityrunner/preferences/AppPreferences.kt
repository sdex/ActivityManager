package com.sdex.activityrunner.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AppPreferences(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME,
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(context, "ads_preferences"),
                SharedPreferencesMigration(context, context.packageName + "_preferences"),
            )
        },
    )

    private val dataStore = context.dataStore
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var isNotExportedDialogShown: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_NOT_EXPORTED_DIALOG_SHOWN] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_NOT_EXPORTED_DIALOG_SHOWN] = value
                }
            }
        }

    var showDonate: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_DONATE] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_DONATE] = value
                }
            }
        }

    val appOpenCounter: Int
        get() = runBlocking { dataStore.data.first()[KEY_OPEN_APP_COUNTER] ?: 0 }

    fun onAppOpened() {
        coroutineScope.launch {
            dataStore.edit { prefs ->
                val current = prefs[KEY_OPEN_APP_COUNTER] ?: 0
                prefs[KEY_OPEN_APP_COUNTER] = current + 1
            }
        }
    }

    /* user preferences */

    var isShowSystemApps: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_SYSTEM_APPS] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_SYSTEM_APPS] = value
                }
            }
        }

    var isShowSystemAppIndicator: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_SYSTEM_APP_LABEL] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_SYSTEM_APP_LABEL] = value
                }
            }
        }

    var isShowDisabledApps: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_DISABLED_APPS] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_DISABLED_APPS] = value
                }
            }
        }

    var isShowDisabledAppIndicator: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_DISABLED_APP_LABEL] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_DISABLED_APP_LABEL] = value
                }
            }
        }

    var showNotExported: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_NOT_EXPORTED] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_NOT_EXPORTED] = value
                }
            }
        }

    var showLineNumbers: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_LINE_NUMBERS] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_LINE_NUMBERS] = value
                }
            }
        }

    @get:AppCompatDelegate.NightMode
    @setparam:AppCompatDelegate.NightMode
    var theme: Int
        get() {
            val value = runBlocking { dataStore.data.first()[KEY_THEME] }
            return value?.toInt() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_THEME] = value.toString()
                }
            }
        }

    var sortBy: String
        get() = runBlocking { dataStore.data.first()[KEY_SORT_BY] } ?: ApplicationModel.NAME
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SORT_BY] = value
                }
            }
        }

    var sortOrder: String
        get() = runBlocking { dataStore.data.first()[KEY_ORDER_BY] } ?: GetApplicationsQuery.ASC
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_ORDER_BY] = value
                }
            }
        }

    var suExecutable: String
        get() = runBlocking { dataStore.data.first()[KEY_SU_EXECUTABLE] } ?: "su"
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SU_EXECUTABLE] = value
                }
            }
        }

    var lastSequenceNumber: Int
        get() = runBlocking { dataStore.data.first()[LAST_SEQUENCE_NUMBER] ?: 0 }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[LAST_SEQUENCE_NUMBER] = value
                }
            }
        }

    var lastBootCount: Int
        get() = runBlocking { dataStore.data.first()[LAST_BOOT_COUNT] ?: -1 }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[LAST_BOOT_COUNT] = value
                }
            }
        }

    private companion object {

        const val PREFERENCES_NAME = "user_preferences"

        val KEY_NOT_EXPORTED_DIALOG_SHOWN = booleanPreferencesKey("not_exported_dialog_shown")
        val KEY_SHOW_DONATE = booleanPreferencesKey("show_donate")
        val KEY_OPEN_APP_COUNTER = intPreferencesKey("open_app_counter")
        val KEY_SHOW_LINE_NUMBERS = booleanPreferencesKey("show_line_numbers")

        val KEY_SHOW_NOT_EXPORTED = booleanPreferencesKey("advanced_not_exported")
        val KEY_SHOW_SYSTEM_APPS = booleanPreferencesKey("show_system_apps")
        val KEY_SHOW_SYSTEM_APP_LABEL = booleanPreferencesKey("advanced_system_app")
        val KEY_SHOW_DISABLED_APPS = booleanPreferencesKey("show_disabled_apps")
        val KEY_SHOW_DISABLED_APP_LABEL = booleanPreferencesKey("advanced_disabled_app")
        val KEY_THEME = stringPreferencesKey("appearance_theme")

        val KEY_SORT_BY = stringPreferencesKey("sort_by")
        val KEY_ORDER_BY = stringPreferencesKey("order_by")
        val KEY_SU_EXECUTABLE = stringPreferencesKey("su_executable")

        val LAST_SEQUENCE_NUMBER = intPreferencesKey("last_sequence_number")
        val LAST_BOOT_COUNT = intPreferencesKey("last_boot_count")
    }
}
