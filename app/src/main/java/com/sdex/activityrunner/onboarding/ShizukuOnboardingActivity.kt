package com.sdex.activityrunner.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityShizukuOnboardingBinding
import com.sdex.activityrunner.util.IntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShizukuOnboardingActivity : BaseActivity() {

    private val viewModel: ShizukuOnboardingViewModel by viewModels()
    private lateinit var binding: ActivityShizukuOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShizukuOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        title = getString(R.string.onboarding_title)

        binding.intro.apply {
            text = HtmlCompat.fromHtml(
                getString(R.string.onboarding_intro, SHIZUKU_REPO_URL),
                HtmlCompat.FROM_HTML_MODE_LEGACY,
            )
            movementMethod = LinkMovementMethod.getInstance()
        }

        binding.step1Action.setOnClickListener { openShizuku() }
        binding.step2Action.setOnClickListener { viewModel.requestShizukuPermission() }
        binding.actionDone.setOnClickListener { finish() }

        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle)
                .collect(::render)
        }
    }

    override fun onResume() {
        super.onResume()
        // Shizuku may have been installed/started or a permission granted while we were away.
        viewModel.refresh()
    }

    private fun render(state: ShizukuOnboardingState) {
        binding.step1Action.setText(
            if (state.shizukuInstalled) {
                R.string.onboarding_action_open_shizuku
            } else {
                R.string.onboarding_action_install_shizuku
            },
        )
        bindStep(
            state,
            OnboardingStep.INSTALL_SHIZUKU,
            binding.step1Badge,
            "1",
            binding.step1Card,
            binding.step1Action,
        )
        bindStep(
            state,
            OnboardingStep.GRANT_PERMISSION,
            binding.step2Badge,
            "2",
            binding.step2Card,
            binding.step2Action,
        )
        binding.actionDone.isEnabled = state.isComplete
    }

    private fun bindStep(
        state: ShizukuOnboardingState,
        step: OnboardingStep,
        badge: TextView,
        number: String,
        card: View,
        vararg actions: MaterialButton,
    ) {
        val status = state.statusOf(step)
        badge.text = if (status == StepStatus.COMPLETE)
            getString(R.string.onboarding_step_done)
        else
            number
        card.alpha = if (status == StepStatus.LOCKED) LOCKED_ALPHA else 1f
        actions.forEach { it.isEnabled = status == StepStatus.ACTIVE }
    }

    private fun openShizuku() {
        val launchIntent = packageManager.getLaunchIntentForPackage(SHIZUKU_PACKAGE)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            IntentUtils.openBrowser(this, SHIZUKU_URL)
        }
    }

    companion object {

        private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        private const val SHIZUKU_URL = "https://shizuku.rikka.app/"
        private const val SHIZUKU_REPO_URL = "https://github.com/RikkaApps/Shizuku"
        private const val LOCKED_ALPHA = 0.5f

        fun intent(context: Context) = Intent(context, ShizukuOnboardingActivity::class.java)
    }
}
