package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.databinding.ActivitySettingsBinding
import com.sdex.commons.BaseActivity

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
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
