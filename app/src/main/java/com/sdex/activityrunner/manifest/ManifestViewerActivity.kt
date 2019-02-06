package com.sdex.activityrunner.manifest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.ThemeHelper
import com.sdex.commons.BaseActivity
import com.sdex.highlightjs.models.Language
import kotlinx.android.synthetic.main.activity_manifest_viewer.*

class ManifestViewerActivity : BaseActivity() {

  private val viewModel: ManifestViewModel by lazy {
    ViewModelProviders.of(this).get(ManifestViewModel::class.java)
  }
  private var appPackageName: String? = null

  override fun getLayout(): Int {
    return R.layout.activity_manifest_viewer
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    try {
      super.onCreate(savedInstanceState)
    } catch (e: Exception) {
      // probably android.webkit.WebViewFactory.MissingWebViewPackageException
      Toast.makeText(this, R.string.error_failed_to_instantiate_web_view, Toast.LENGTH_SHORT).show()
      finish()
      return
    }
    enableBackButton()

    progress.show()

    val themeHelper = ThemeHelper()

    highlightView.setBackgroundColor(Color.TRANSPARENT)
    highlightView.highlightLanguage = Language.XML
    highlightView.theme = themeHelper.getWebViewTheme(currentTheme)
    highlightView.setShowLineNumbers(true)
    highlightView.setZoomSupportEnabled(true)
    highlightView.setOnContentChangedListener {
      progress.hide()
    }

    appPackageName = intent.getStringExtra(ARG_PACKAGE_NAME)
    var name = intent.getStringExtra(ARG_NAME)

    if (appPackageName.isNullOrEmpty()) {
      appPackageName = BuildConfig.APPLICATION_ID
      name = getString(R.string.app_name)
    }

    title = name

    viewModel.loadManifest(appPackageName!!).observe(this, Observer {
      if (it == null) {
        Toast.makeText(this, R.string.error_failed_to_open_manifest, Toast.LENGTH_SHORT).show()
        finish()
      } else {
        highlightView.setSource(it)
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.manifest_viewer, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_share -> {
        val shareProvider = ShareProvider()
        shareProvider.share(this, appPackageName!!)
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

  companion object {

    private const val ARG_PACKAGE_NAME = "arg_package_name"
    private const val ARG_NAME = "arg_name"

    fun start(context: Context, model: ApplicationModel) {
      val starter = Intent(context, ManifestViewerActivity::class.java)
      starter.putExtra(ARG_PACKAGE_NAME, model.packageName)
      starter.putExtra(ARG_NAME, model.name)
      context.startActivity(starter)
    }
  }
}
