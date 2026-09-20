package com.sdex.activityrunner.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.RootConfigDialog
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivitySettingsBinding
import com.sdex.activityrunner.intent.analyzer.IntentInterceptor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    private val viewModel by viewModels<SettingsViewModel>()

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

        binding.switchInterceptIntents.isChecked = appPreferences.isInterceptIntents
        binding.switchInterceptIntents.setOnCheckedChangeListener { _, isChecked ->
            appPreferences.isInterceptIntents = isChecked
            IntentInterceptor.setEnabled(this, isChecked)
        }
        binding.interceptIntents.setOnClickListener {
            binding.switchInterceptIntents.isChecked = !binding.switchInterceptIntents.isChecked
        }

        binding.rootConfig.setOnClickListener {
            RootConfigDialog.newInstance()
                .show(supportFragmentManager, RootConfigDialog.TAG)
        }

        binding.clearCache.setOnClickListener {
            ClearCacheDialog.newInstance()
                .show(supportFragmentManager, ClearCacheDialog.TAG)
        }

        lifecycleScope.launch {
            var wasClearingCache = false
            viewModel.isClearingCache.flowWithLifecycle(lifecycle)
                .collect { isClearingCache ->
                    binding.clearCache.isEnabled = !isClearingCache
                    binding.clearCacheProgress.isVisible = isClearingCache
                    if (wasClearingCache && !isClearingCache) {
                        Toast.makeText(
                            this@SettingsActivity,
                            R.string.pref_advanced_clear_cache_done,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    wasClearingCache = isClearingCache
                }
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
