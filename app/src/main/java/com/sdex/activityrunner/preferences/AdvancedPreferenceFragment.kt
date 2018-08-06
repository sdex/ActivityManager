package com.sdex.activityrunner.preferences

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import android.widget.Toast
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.root.GetRootDialog
import com.sdex.activityrunner.premium.GetPremiumDialog
import com.sdex.activityrunner.util.CheckRootTask
import com.sdex.commons.ads.AppPreferences

class AdvancedPreferenceFragment : PreferenceFragmentCompat() {

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.pref_advanced, rootKey)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val appPreferences = AppPreferences(activity)
    val rootIntegration = findPreference(AdvancedPreferences.KEY_ROOT_INTEGRATION)
      as SwitchPreferenceCompat
    rootIntegration.setOnPreferenceChangeListener { _, newValue ->
      if (newValue is Boolean) {
        if (!newValue) {
          return@setOnPreferenceChangeListener true
        }
      }
      if (appPreferences.isProVersion) {
        val checkRootTask = CheckRootTask(object : CheckRootTask.Callback {
          override fun onStatusChanged(status: Int) {
            if (activity != null && !activity!!.isFinishing && isAdded) {
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
        val dialog = GetRootDialog.newInstance()
        dialog.show(childFragmentManager, GetPremiumDialog.TAG)
        return@setOnPreferenceChangeListener false
      }
    }
  }
}