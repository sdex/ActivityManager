package com.sdex.activityrunner.preferences

import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.sdex.activityrunner.R
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.activityrunner.util.CheckRootTask
import com.sdex.commons.ads.AppPreferences

class AdvancedPreferenceFragment : PreferenceFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.pref_advanced)
    setHasOptionsMenu(true)
    val appPreferences = AppPreferences(activity)
    val rootIntegration = findPreference(SettingsActivity.KEY_ADVANCED_ROOT_INTEGRATION) as SwitchPreference
    rootIntegration.setOnPreferenceChangeListener { _, _ ->
      if (appPreferences.isProVersion) {
        val checkRootTask = CheckRootTask(object : CheckRootTask.Callback {
          override fun onStatusChanged(status: Int) {
            if (activity != null && isAdded) {
              if (status != CheckRootTask.RESULT_OK) {
                rootIntegration.isChecked = false
                Toast.makeText(activity, R.string.settings_error_root_not_granted,
                  Toast.LENGTH_SHORT).show()
              }
            }
          }
        })
        checkRootTask.execute()
        return@setOnPreferenceChangeListener true
      } else {
        AlertDialog.Builder(activity)
          .setTitle(R.string.pro_version_dialog_title)
          .setMessage(R.string.pro_version_unlock_root_integration)
          .setPositiveButton(R.string.pro_version_get
          ) { _, _ -> PurchaseActivity.start(activity) }
          .show()
        return@setOnPreferenceChangeListener false
      }
    }
  }
}