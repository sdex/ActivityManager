package com.sdex.activityrunner.preferences

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.TaskStackBuilder
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.sdex.activityrunner.MainActivity
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.fitsSystemWindowsInsets
import com.sdex.activityrunner.preferences.AppPreferences.Companion.KEY_THEME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themePreference = findPreference(KEY_THEME) as ListPreference?
        // themePreference?.summary = getCurrentTheme(appPreferences.theme)
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(newValue.toString().toInt())
            // themePreference.summary = getCurrentTheme(newValue.toString().toInt())
            return@setOnPreferenceChangeListener true
        }

        // (findPreference(KEY_SHOW_SYSTEM_APPS) as SwitchPreferenceCompat?)
        //     ?.setOnPreferenceChangeListener { _, _ ->
        //         // restartApp()
        //         return@setOnPreferenceChangeListener true
        //     }
        //
        // (findPreference(KEY_SHOW_DISABLED_APPS) as SwitchPreferenceCompat?)
        //     ?.setOnPreferenceChangeListener { _, _ ->
        //         // restartApp()
        //         return@setOnPreferenceChangeListener true
        //     }
    }


    override fun onCreateRecyclerView(
        inflater: LayoutInflater, parent: ViewGroup, savedInstanceState: Bundle?): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.fitsSystemWindowsInsets(left = true, right = true, bottom = true)
        return recyclerView
    }

    private fun restartApp() {
        TaskStackBuilder.create(requireContext())
            .addNextIntent(Intent(requireContext(), MainActivity::class.java))
            .addNextIntent(Intent(requireContext(), SettingsActivity::class.java))
            .startActivities()
        requireActivity().finish()
    }

    private fun getCurrentTheme(@AppCompatDelegate.NightMode theme: Int): CharSequence {
        val values = resources.getStringArray(R.array.pref_appearance_theme_list_titles)
        return when (theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> values[1]
            AppCompatDelegate.MODE_NIGHT_YES -> values[2]
            else -> values[0]
        }
    }
}
