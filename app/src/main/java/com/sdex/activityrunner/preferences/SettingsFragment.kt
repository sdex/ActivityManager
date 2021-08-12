package com.sdex.activityrunner.preferences

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sdex.activityrunner.R
import com.sdex.activityrunner.preferences.AppPreferences.Companion.KEY_ROOT_INTEGRATION
import com.sdex.activityrunner.preferences.AppPreferences.Companion.KEY_THEME
import com.sdex.activityrunner.util.CheckRootTask

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_advanced, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootIntegration = findPreference(KEY_ROOT_INTEGRATION) as SwitchPreferenceCompat?
        rootIntegration?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue is Boolean) {
                if (!newValue) {
                    return@setOnPreferenceChangeListener true
                }
            }
            checkRoot(rootIntegration)
            return@setOnPreferenceChangeListener true
        }

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

    private fun checkRoot(rootIntegration: SwitchPreferenceCompat) {
        val checkRootTask = CheckRootTask(object : CheckRootTask.Callback {
            override fun onStatusChanged(status: Int) {
                if (activity != null && !activity!!.isFinishing && isAdded) {
                    if (status != CheckRootTask.RESULT_OK) {
                        rootIntegration.isChecked = false
                        Toast.makeText(
                            activity, R.string.settings_error_root_not_granted,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
        checkRootTask.execute()
    }
}
