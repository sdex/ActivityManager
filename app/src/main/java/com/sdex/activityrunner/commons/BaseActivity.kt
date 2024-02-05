package com.sdex.activityrunner.commons

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sdex.activityrunner.R

open class BaseActivity : AppCompatActivity() {

    protected fun setupToolbar(isBackButtonEnabled: Boolean = false) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    var subTitle: CharSequence?
        get() {
            return supportActionBar?.subtitle
        }
        set(value) {
            supportActionBar?.subtitle = value
        }
}
