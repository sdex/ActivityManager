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
    supportFragmentManager.beginTransaction()
      .replace(R.id.content, SettingsFragment())
      .commitNow()
  }

  companion object {

    fun start(context: Context) {
      val starter = Intent(context, SettingsActivity::class.java)
      context.startActivity(starter)
    }
  }
}
