package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.commons.BaseActivity

class SettingsActivity : BaseActivity() {

  override fun getLayout(): Int {
    return R.layout.activity_settings
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .add(R.id.content, AdvancedPreferenceFragment())
        .commit()
    }
  }

  companion object {

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

    fun start(context: Context) {
      val starter = Intent(context, SettingsActivity::class.java)
      context.startActivity(starter)
    }
  }
}
