package com.sdex.activityrunner.manifest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ActivityManifestViewerBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.UIUtils
import com.sdex.highlightjs.models.Language
import com.sdex.highlightjs.models.Theme


class ManifestViewerActivity : BaseActivity() {

    private val viewModel by viewModels<ManifestViewModel>()
    private val appPreferences by lazy { AppPreferences(this) }
    private lateinit var binding: ActivityManifestViewerBinding
    private lateinit var appPackageName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityManifestViewerBinding.inflate(layoutInflater)
        } catch (e: Exception) {
            // probably android.webkit.WebViewFactory.MissingWebViewPackageException
            Toast.makeText(this, R.string.error_failed_to_instantiate_web_view, Toast.LENGTH_SHORT)
                .show()
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
                Theme.DARKULA
            } else {
                Theme.GITHUB_GIST
            }
            setShowLineNumbers(true)
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.manifest_viewer, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchViewItem.actionView as SearchView
        searchView.queryHint = getString(R.string.action_search_manifest_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isEmpty()) {
                        binding.highlightView.clearMatches()
                    } else {
                        binding.highlightView.findAllAsync(it)
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) binding.highlightView.clearMatches()
                }
                return false
            }

        })
        searchViewItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                UIUtils.setMenuItemsVisibility(menu, item, false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.highlightView.clearMatches()
                UIUtils.setMenuItemsVisibility(menu, true)
                invalidateOptionsMenu()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val shareProvider = ShareProvider()
                shareProvider.share(this, appPackageName)
                true
            }
            R.id.action_help -> {
                val url = "https://developer.android.com/guide/topics/manifest/manifest-intro"
                IntentUtils.openBrowser(this, url)
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
