package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

import com.sdex.activityrunner.R
import com.sdex.activityrunner.util.Utils

class SettingsActivity : AppCompatPreferenceActivity() {

  override fun onIsMultiPane(): Boolean {
    return Utils.isXLargeTablet(this)
  }

  override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
    loadHeadersFromResource(R.xml.pref_headers, target)
  }

  override fun isValidFragment(fragmentName: String): Boolean {
    return (PreferenceFragment::class.java.name == fragmentName
      || SortPreferenceFragment::class.java.name == fragmentName
      || AdvancedPreferenceFragment::class.java.name == fragmentName)
  }

  companion object {

    const val NORMAL = 0
    const val ADVANCED = 1

    const val KEY_SORT_ORDER = "sort_order"
    const val KEY_SORT_ORDER_DEFAULT = "0"
    const val KEY_SORT_BY = "sort_by"
    const val KEY_SORT_BY_DEFAULT = "0"
    const val KEY_SORT_CASE_SENSITIVE = "sort_case_sensitive"
    const val KEY_SORT_CASE_SENSITIVE_DEFAULT = false
    const val KEY_ADVANCED_NOT_EXPORTED = "advanced_not_exported"
    const val KEY_ADVANCED_NOT_EXPORTED_DEFAULT = false
    const val KEY_ADVANCED_ROOT_INTEGRATION = "advanced_root_integration"
    const val KEY_ADVANCED_ROOT_INTEGRATION_DEFAULT = false

    fun start(context: Context, state: Int) {
      val starter = Intent(context, SettingsActivity::class.java)
      if (state == ADVANCED) {
        starter.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
          "com.sdex.activityrunner.preferences.AdvancedPreferenceFragment")
      }
      context.startActivity(starter)
    }
  }
}
