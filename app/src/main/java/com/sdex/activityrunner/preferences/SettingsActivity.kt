package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.RootConfigDialog
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        setTitle(R.string.action_settings)

        binding.switchLaunchToast.isChecked = appPreferences.isShowLaunchToast
        binding.switchLaunchToast.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isShowLaunchToast = isChecked
        }
        binding.launchToast.setOnClickListener {
            binding.switchLaunchToast.isChecked = !binding.switchLaunchToast.isChecked
        }

        binding.rootConfig.setOnClickListener {
            RootConfigDialog.newInstance()
                .show(supportFragmentManager, RootConfigDialog.TAG)
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
