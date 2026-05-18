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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(context, "ads_preferences"),
            SharedPreferencesMigration(context, context.packageName + "_preferences"),
        )
    },
)

class AppPreferencesImpl(context: Context) : AppPreferences {

    private val dataStore = context.dataStore
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override val preferences: Flow<PreferencesState> = dataStore.data.map {
        PreferencesState(
            isShowNonExportedActivities = it[KEY_SHOW_NOT_EXPORTED] ?: false,
            isShowSystemApps = it[KEY_SHOW_SYSTEM_APPS] ?: true,
            isShowSystemAppIndicator = it[KEY_SHOW_SYSTEM_APP_LABEL] ?: false,
            isShowDisabledApps = it[KEY_SHOW_DISABLED_APPS] ?: true,
            isShowDisabledAppIndicator = it[KEY_SHOW_DISABLED_APP_LABEL] ?: false,
            theme = it[KEY_THEME]?.toInt() ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            sortBy = it[KEY_SORT_BY] ?: ApplicationModel.NAME,
            sortOrder = it[KEY_ORDER_BY] ?: GetApplicationsQuery.ASC,
        )
    }.distinctUntilChanged()

    override val displayConfig: Flow<DisplayConfig> = dataStore.data
        .map { prefs ->
            DisplayConfig(
                showSystemApps = prefs[KEY_SHOW_SYSTEM_APPS] ?: true,
                showSystemAppIndicator = prefs[KEY_SHOW_SYSTEM_APP_LABEL] ?: false,
                showDisabledApps = prefs[KEY_SHOW_DISABLED_APPS] ?: true,
                showDisabledAppIndicator = prefs[KEY_SHOW_DISABLED_APP_LABEL] ?: false,
                sortBy = prefs[KEY_SORT_BY] ?: ApplicationModel.NAME,
                sortOrder = prefs[KEY_ORDER_BY] ?: GetApplicationsQuery.ASC,
            )
        }.distinctUntilChanged()

    override var isNotExportedDialogShown: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_NOT_EXPORTED_DIALOG_SHOWN] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_NOT_EXPORTED_DIALOG_SHOWN] = value
                }
            }
        }

    override val appOpenCounter: Int
        get() = runBlocking { dataStore.data.first()[KEY_OPEN_APP_COUNTER] ?: 0 }

    override fun onAppOpened() {
        coroutineScope.launch {
            dataStore.edit { prefs ->
                val current = prefs[KEY_OPEN_APP_COUNTER] ?: 0
                prefs[KEY_OPEN_APP_COUNTER] = current + 1
            }
        }
    }

    /* user preferences */

    override var isShowSystemApps: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_SYSTEM_APPS] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_SYSTEM_APPS] = value
                }
            }
        }

    override var isShowSystemAppIndicator: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_SYSTEM_APP_LABEL] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_SYSTEM_APP_LABEL] = value
                }
            }
        }

    override var isShowDisabledApps: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_DISABLED_APPS] ?: true }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_DISABLED_APPS] = value
                }
            }
        }

    override var isShowDisabledAppIndicator: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_DISABLED_APP_LABEL] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_DISABLED_APP_LABEL] = value
                }
            }
        }

    override var showNotExported: Boolean
        get() = runBlocking { dataStore.data.first()[KEY_SHOW_NOT_EXPORTED] ?: false }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SHOW_NOT_EXPORTED] = value
                }
            }
        }

    override var showLineNumbers: Boolean
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
    override var theme: Int
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

    override var sortBy: String
        get() = runBlocking { dataStore.data.first()[KEY_SORT_BY] } ?: ApplicationModel.NAME
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SORT_BY] = value
                }
            }
        }

    override var sortOrder: String
        get() = runBlocking { dataStore.data.first()[KEY_ORDER_BY] } ?: GetApplicationsQuery.ASC
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_ORDER_BY] = value
                }
            }
        }

    override var suExecutable: String
        get() = runBlocking { dataStore.data.first()[KEY_SU_EXECUTABLE] } ?: "su"
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[KEY_SU_EXECUTABLE] = value
                }
            }
        }

    override var lastSequenceNumber: Int
        get() = runBlocking { dataStore.data.first()[LAST_SEQUENCE_NUMBER] ?: -1 }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[LAST_SEQUENCE_NUMBER] = value
                }
            }
        }

    override var lastBootCount: Int
        get() = runBlocking { dataStore.data.first()[LAST_BOOT_COUNT] ?: -1 }
        set(value) {
            coroutineScope.launch {
                dataStore.edit { prefs ->
                    prefs[LAST_BOOT_COUNT] = value
                }
            }
        }

    private companion object {

        val KEY_NOT_EXPORTED_DIALOG_SHOWN = booleanPreferencesKey("not_exported_dialog_shown")
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
