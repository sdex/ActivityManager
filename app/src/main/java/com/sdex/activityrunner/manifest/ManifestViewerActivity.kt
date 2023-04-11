package com.sdex.activityrunner.manifest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityManifestViewerBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.highlightjs.models.Language
import com.sdex.activityrunner.util.highlightjs.models.Theme
import com.yupo.browserfiplib.FiPSearchView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class ManifestViewerActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private val viewModel by viewModels<ManifestViewModel>()
    private lateinit var binding: ActivityManifestViewerBinding
    private lateinit var appPackageName: String
    private val saveLocationLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/xml")) {
            it?.let { uri -> viewModel.export(uri) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityManifestViewerBinding.inflate(layoutInflater)
        } catch (e: Exception) {
            // probably android.webkit.WebViewFactory.MissingWebViewPackageException
            Toast.makeText(
                this,
                R.string.error_failed_to_instantiate_web_view,
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        binding.progress.show()

        binding.highlightView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            highlightLanguage = Language.XML
            theme = if (isNightTheme(appPreferences.theme)) {
                Theme.DARK
            } else {
                Theme.LIGHT
            }
            setShowLineNumbers(appPreferences.showLineNumbers)
            setZoomSupportEnabled(true)
            setOnContentChangedListener {
                binding.progress.hide()
            }
        }

        appPackageName = intent.getStringExtra(ARG_PACKAGE_NAME) ?: ""
        var name = intent.getStringExtra(ARG_NAME)

        if (appPackageName.isEmpty()) {
            appPackageName = BuildConfig.APPLICATION_ID
            name = getString(R.string.app_name)
        }

        title = name

        viewModel.manifestLiveData.observe(this) {
            if (it == null) {
                Toast.makeText(
                    this,
                    R.string.error_failed_to_open_manifest,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                binding.highlightView.setSource(it)
            }
        }

        viewModel.loadManifest(appPackageName)
        setupFindInPage()
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                if (binding.fipContainer.visibility == View.VISIBLE) {
                    hideFindInPage()
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupFindInPage() {
        binding.fip.setupSearchComponent(binding.highlightView)
        binding.fip.onNavigationClicked = {
            if (it == FiPSearchView.ClickEvent.CLOSE) {
                hideFindInPage()
            }
        }
    }

    private fun hideFindInPage() {
        showToolbar()
        binding.highlightView.clearMatches()
        binding.fip.onActionViewCollapsed()
        binding.fipContainer.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manifest_viewer, menu)
        menu.findItem(R.id.action_line_numbers).isChecked = appPreferences.showLineNumbers
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export -> {
                saveLocationLauncher.launch("AndroidManifest($appPackageName).xml")
                true
            }

            R.id.action_help -> {
                val url = "https://developer.android.com/guide/topics/manifest/manifest-intro"
                IntentUtils.openBrowser(this, url)
                true
            }
            R.id.action_search -> {
                hideToolbar()
                binding.fipContainer.visibility = View.VISIBLE
                binding.fip.onActionViewExpanded()
                false
            }

            R.id.action_line_numbers -> {
                item.isChecked = !item.isChecked
                appPreferences.showLineNumbers = item.isChecked
                binding.highlightView.setShowLineNumbers(item.isChecked)
                binding.highlightView.setSource(viewModel.manifestLiveData.value)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isNightTheme(@AppCompatDelegate.NightMode theme: Int) =
        theme == AppCompatDelegate.MODE_NIGHT_YES ||
            (theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM &&
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES))

    override fun onDestroy() {
        super.onDestroy()
        binding.fip.release()
        binding.highlightView.setOnContentChangedListener(null)
    }

    private fun hideToolbar() {
        findViewById<Toolbar>(R.id.toolbar).isVisible = false
    }

    private fun showToolbar() {
        findViewById<Toolbar>(R.id.toolbar).isVisible = true
    }

    companion object {

        private const val ARG_PACKAGE_NAME = "arg_package_name"
        private const val ARG_NAME = "arg_name"

        fun start(context: Context, model: ApplicationModel) {
            context.startActivity(Intent(context, ManifestViewerActivity::class.java).apply {
                putExtra(ARG_PACKAGE_NAME, model.packageName)
                putExtra(ARG_NAME, model.name)
            })
        }
    }
}
