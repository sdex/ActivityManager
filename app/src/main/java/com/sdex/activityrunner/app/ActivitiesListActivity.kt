package com.sdex.activityrunner.app

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.ui.SnackbarContainerActivity
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_activities_list.*

class ActivitiesListActivity : BaseActivity(), SnackbarContainerActivity {

  private val appPreferences: AppPreferences by lazy { AppPreferences(this) }
  private val viewModel: ActivitiesListViewModel by lazy {
    ViewModelProviders.of(this).get(ActivitiesListViewModel::class.java)
  }

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
    val adapter = ActivitiesListAdapter(this)
    list.adapter = adapter

    viewModel.getItems(item.packageName).observe(this, Observer {
      adapter.submitList(it)
      val size = it!!.size
      setSubtitle(resources.getQuantityString(R.plurals.activities_count, size, size))
      if (size == 0) {
        empty.visibility = VISIBLE
      } else {
        empty.visibility = GONE
      }
    })

    isShowNotExported = appPreferences.showNotExported

    turnOnAdvanced.setOnClickListener {
      appPreferences.showNotExported = true
      viewModel.getItems(item.packageName)
    }

    if (!appPreferences.showNotExported && !appPreferences.isNotExportedDialogShown) {
      appPreferences.isNotExportedDialogShown = true
      val dialog = EnableNotExportedActivitiesDialog()
      dialog.show(supportFragmentManager, EnableNotExportedActivitiesDialog.TAG)
    }
  }

  override fun onStart() {
    super.onStart()
    if (appPreferences.showNotExported != isShowNotExported) {
      val viewModel = ViewModelProviders.of(this).get(ActivitiesListViewModel::class.java)
      val item = intent.getSerializableExtra(ARG_APPLICATION) as ApplicationModel?
      if (item != null) {
        viewModel.getItems(item.packageName)
      }
    }
  }

  override fun getView(): View {
    return container
  }

  override fun getActivity(): Activity {
    return this
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
