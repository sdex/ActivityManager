package com.sdex.activityrunner.manifest

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.pddstudio.highlightjs.models.Language
import com.pddstudio.highlightjs.models.Theme
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_manifest_viewer.*

class ManifestViewerActivity : BaseActivity() {

  override fun getLayout(): Int {
    return R.layout.activity_manifest_viewer
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    progress.show()

    highlightView.highlightLanguage = Language.XML
    highlightView.theme = Theme.GITHUB_GIST
    highlightView.setShowLineNumbers(true)
    highlightView.setZoomSupportEnabled(true)
    highlightView.setOnContentChangedListener {
      progress.hide()
    }

    val packageName = intent.getStringExtra(ARG_PACKAGE_NAME)
    val name = intent.getStringExtra(ARG_NAME)

    if (packageName == null) {
      return
    }

    title = name

    ViewModelProviders.of(this).get(ManifestViewModel::class.java)
      .loadManifest(packageName).observe(this, Observer {
        highlightView.setSource(it)
      })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.manifest_viewer, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
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

    fun start(context: Context, packageName: String, name: String?) {
      val starter = Intent(context, ManifestViewerActivity::class.java)
      starter.putExtra(ARG_PACKAGE_NAME, packageName)
      starter.putExtra(ARG_NAME, name)
      context.startActivity(starter)
    }
  }
}
