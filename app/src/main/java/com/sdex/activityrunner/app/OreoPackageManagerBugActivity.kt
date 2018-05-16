package com.sdex.activityrunner.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import com.sdex.activityrunner.R
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_oreo_package_manager_bug.*

class OreoPackageManagerBugActivity : FragmentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_oreo_package_manager_bug)

    val preferences = AppPreferences(this)

    proceed.setOnClickListener {
      preferences.preferences
        .edit()
        .putBoolean(KEY, true)
        .apply()
      finish()
    }

    openApps.setOnClickListener {
      finish()
      try {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  companion object {

    const val KEY = "oreo_bug_warning_shown"
  }
}
