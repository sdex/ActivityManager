package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private val binding by lazy {
    ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.content.id, SettingsFragment())
                .commitNow()
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, SettingsActivity::class.java)
            context.startActivity(starter)
        }
    }
}
