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

    const val KEY_ADVANCED_SYSTEM_APP = "advanced_system_app"
    const val KEY_ADVANCED_SYSTEM_APP_DEFAULT = false

    fun start(context: Context) {
      val starter = Intent(context, SettingsActivity::class.java)
      context.startActivity(starter)
    }
  }
}
