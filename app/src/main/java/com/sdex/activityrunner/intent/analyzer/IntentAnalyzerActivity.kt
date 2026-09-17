package com.sdex.activityrunner.intent.analyzer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityIntentAnalyzerBinding
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.intent.LaunchParams
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Shows everything that can be read off an incoming intent.
 *
 * The screen is reached through the `IntentInterceptorActivity` manifest alias, which the user
 * enables in the settings to make the app appear in the system share and open dialogs. See
 * [IntentInterceptor].
 */
@AndroidEntryPoint
class IntentAnalyzerActivity : BaseActivity() {

    private val viewModel: IntentAnalyzerViewModel by viewModels()

    private lateinit var binding: ActivityIntentAnalyzerBinding

    private val adapter = IntentAnalyzerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentAnalyzerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        setTitle(R.string.intent_analyzer_activity)

        binding.list.adapter = adapter

        viewModel.analyze(
            intent = intent,
            source = IntentSource(
                referrer = referrer,
                callingPackage = callingPackage,
                callingActivity = callingActivity,
            ),
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { adapter.setItems(it) }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.intent_analyzer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_in_launcher -> {
                openInLauncher()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Hands the intent over to the launcher, warning first when it carries uris the launcher cannot
     * pass on, so the user is not left guessing why the target application sees nothing.
     */
    private fun openInLauncher() {
        val launchParams = viewModel.getLaunchParams() ?: return
        val unsupportedUris = viewModel.getUnsupportedUris()
        if (unsupportedUris.isEmpty()) {
            startLauncher(launchParams)
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.analyzer_dialog_unsupported_uris_title)
            .setMessage(
                getString(
                    R.string.analyzer_dialog_unsupported_uris_message,
                    unsupportedUris.joinToString("\n"),
                ),
            )
            .setPositiveButton(R.string.analyzer_action_open_in_launcher) { _, _ ->
                startLauncher(launchParams)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun startLauncher(launchParams: LaunchParams) {
        IntentBuilderActivity.start(this, launchParams)
    }
}
