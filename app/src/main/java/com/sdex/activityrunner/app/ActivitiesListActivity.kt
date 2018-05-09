package com.sdex.activityrunner.app

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.activity.ActivityModel
import com.sdex.activityrunner.db.application.ApplicationModel
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.shortcut.AddShortcutDialogFragment
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.RunActivityTask
import com.sdex.activityrunner.util.addDivider
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_activities_list.*

class ActivitiesListActivity : BaseActivity(), ActivitiesListAdapter.Callback {

  private var advancedPreferences: AdvancedPreferences? = null

  override fun getLayout(): Int {
    return R.layout.activity_activities_list
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val item = intent.getSerializableExtra(ARG_APPLICATION) as ApplicationModel
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
    })

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    advancedPreferences = AdvancedPreferences(sharedPreferences)
  }

  override fun showShortcutDialog(item: ActivityModel) {
    val dialog = AddShortcutDialogFragment.newInstance(item)
    dialog.show(supportFragmentManager, AddShortcutDialogFragment.TAG)
  }

  override fun launchActivity(item: ActivityModel) {
    if (item.exported) {
      IntentUtils.launchActivity(this, item.componentName, item.name)
    } else {
      tryRunWithRoot(item)
    }
  }

  override fun launchActivityWithParams(item: ActivityModel) {
    IntentBuilderActivity.start(this, item)
  }

  private fun tryRunWithRoot(item: ActivityModel) {
    if (advancedPreferences!!.isRootIntegrationEnabled) {
      val runActivityTask = RunActivityTask(item.componentName)
      runActivityTask.execute()
    } else {
      Snackbar.make(container, R.string.settings_error_root_not_active, Snackbar.LENGTH_LONG)
        .setAction(R.string.action_settings
        ) { SettingsActivity.start(this@ActivitiesListActivity, SettingsActivity.ADVANCED) }
        .setActionTextColor(ContextCompat.getColor(this, R.color.yellow))
        .show()
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
