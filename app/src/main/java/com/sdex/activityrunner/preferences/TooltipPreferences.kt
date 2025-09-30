package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.Delegates

class TooltipPreferences(context: Context) {

    private var sharedPreferences: SharedPreferences by Delegates.notNull()

    init {
        sharedPreferences = context.getSharedPreferences("tooltips_states", Context.MODE_PRIVATE)
    }

    var showChangeIcon: Boolean
        get() = sharedPreferences.getBoolean(SHORTCUT_ICON_CHANGE, SHORTCUT_ICON_CHANGE_DEFAULT)
        set(show) {
            sharedPreferences.edit { putBoolean(SHORTCUT_ICON_CHANGE, show) }
        }

    companion object {
        const val SHORTCUT_ICON_CHANGE = "shortcut_icon_change"
        const val SHORTCUT_ICON_CHANGE_DEFAULT = true
    }
}
