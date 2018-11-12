package com.sdex.commons

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.sdex.activityrunner.R
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.sdex.commons.util.ThemeHelper

abstract class BaseActivity : AppCompatActivity() {

  private val advancedPreferences: AdvancedPreferences by lazy {
    AdvancedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
  }
  var currentTheme: String? = null

  abstract fun getLayout(): Int

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val themeHelper = ThemeHelper()
    currentTheme = advancedPreferences.getTheme
    themeHelper.setTheme(this, currentTheme)
    setContentView(getLayout())
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    if (toolbar != null) {
      setSupportActionBar(toolbar)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    if (itemId == android.R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun setTitle(title: CharSequence) {
    super.setTitle(title)
    if (supportActionBar != null) {
      supportActionBar!!.title = title
    }
  }

  fun setSubtitle(subtitle: CharSequence) {
    if (supportActionBar != null) {
      supportActionBar!!.subtitle = subtitle
    }
  }
}
