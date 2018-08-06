package com.sdex.activityrunner.app

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.view.View.GONE
import android.view.View.VISIBLE
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.extensions.config
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.RunActivityTask
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_activities_list.*
import kotlin.properties.Delegates

class ActivitiesListActivity : BaseActivity(), ActivitiesListAdapter.Callback {

  private var advancedPreferences: AdvancedPreferences by Delegates.notNull()
  private var isShowNotExported: Boolean = false

  override fun getLayout(): Int {
    return R.layout.activity_activities_list
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val item = intent.getSerializableExtra(ARG_APPLICATION) as ApplicationModel?
    if (item == null) {
      finish()
      return
    }
    title = item.name
    enableBackButton()
    list.addDivider()
    val adapter = ActivitiesListAdapter(this, this)
    list.adapter = adapter
    val viewModel = ViewModelProviders.of(this).get(ActivitiesListViewModel::class.java)
    viewModel.getItems(item.packageName).observe(this, Observer {
      adapter.submitList(it)
      val size = it!!.size
      setSubtitle(resources.getQuantityString(R.plurals.activities_count, size, size))
      if (size == 0) {
        empty.visibility = VISIBLE
        if (item.activitiesCount == 0) {
          // TODO include app without activities?
        }
      } else {
        empty.visibility = GONE
      }
    })

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    advancedPreferences = AdvancedPreferences(sharedPreferences)
    isShowNotExported = advancedPreferences!!.isShowNotExported

    turnOnAdvanced.setOnClickListener {
      advancedPreferences.showNotExported = true
      viewModel.getItems(item.packageName)
    }

    val appPreferences = AppPreferences(this)
    if (!advancedPreferences.showNotExported && !appPreferences.isNotExportedDialogShown) {
      appPreferences.isNotExportedDialogShown = true
      val dialog = EnableNotExportedActivitiesDialog()
      dialog.show(supportFragmentManager, EnableNotExportedActivitiesDialog.TAG)
    }
  }

  override fun onStart() {
    super.onStart()
    if (advancedPreferences!!.isShowNotExported != isShowNotExported) {
      val viewModel = ViewModelProviders.of(this).get(ActivitiesListViewModel::class.java)
      val item = intent.getSerializableExtra(ARG_APPLICATION) as ApplicationModel?
      if (item != null) {
        viewModel.getItems(item.packageName)
      }
    }
  }

  override fun showShortcutDialog(item: ActivityModel) {
    AddShortcutDialogActivity.start(this, item)
  }

  override fun launchActivity(item: ActivityModel) {
    if (item.exported) {
      IntentUtils.launchActivity(this, item.componentName, item.name)
    } else {
      tryRunWithRoot(item)
    }
  }

  override fun launchActivityWithRoot(item: ActivityModel) {
    tryRunWithRoot(item)
  }

  override fun launchActivityWithParams(item: ActivityModel) {
    IntentBuilderActivity.start(this, item)
  }

  private fun tryRunWithRoot(item: ActivityModel) {
    if (advancedPreferences.isRootIntegrationEnabled) {
      val runActivityTask = RunActivityTask(item.componentName)
      runActivityTask.execute()
    } else {
      val snackbar = Snackbar.make(container, R.string.settings_error_root_not_active,
        Snackbar.LENGTH_LONG)
      snackbar.setAction(R.string.action_settings
      ) { SettingsActivity.start(this@ActivitiesListActivity) }
      snackbar.config()
      snackbar.show()
    }
  }

  companion object {

    const val ARG_APPLICATION = "arg_application"

    fun start(context: Context, item: ApplicationModel) {
      val starter = Intent(context, ActivitiesListActivity::class.java)
      starter.putExtra(ARG_APPLICATION, item)
      context.startActivity(starter)
    }
  }
}
