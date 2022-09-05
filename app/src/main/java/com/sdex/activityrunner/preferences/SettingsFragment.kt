package com.sdex.activityrunner.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.sdex.activityrunner.R
import com.sdex.activityrunner.preferences.AppPreferences.Companion.KEY_THEME

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_advanced, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themePreference = findPreference(KEY_THEME) as ListPreference?
        themePreference?.summary = getCurrentTheme(AppPreferences(requireContext()).theme)
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
            themePreference.summary = getCurrentTheme(newValue.toString().toInt())
            return@setOnPreferenceChangeListener true
        }
    }

    private fun getCurrentTheme(@AppCompatDelegate.NightMode theme: Int): CharSequence? {
        val values = resources.getStringArray(R.array.pref_appearance_theme_list_titles)
        return when (theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> values[1]
            AppCompatDelegate.MODE_NIGHT_YES -> values[2]
            else -> values[0]
        }
    }
}
