package com.sdex.activityrunner.intent.analyzer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.extensions.getFlagsList
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.LaunchParamsListAdapter
import com.sdex.activityrunner.intent.param.None
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_intent_analyzer.*

class IntentAnalyzerActivity : BaseActivity() {

  override fun getLayout(): Int {
    return R.layout.activity_intent_analyzer
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    actionView.text = intent.action ?: None.VALUE
    dataView.text = intent.dataString ?: None.VALUE
    mimeTypeView.text = intent.type ?: None.VALUE

    val categoriesAdapter = LaunchParamsListAdapter()
    categoriesAdapter.setItems(intent.categories)
    listCategoriesView.adapter = categoriesAdapter

    val flagsAdapter = LaunchParamsListAdapter()
    flagsAdapter.setItems(intent.getFlagsList())
    listFlagsView.adapter = flagsAdapter

    val extrasAdapter = LaunchParamsExtraListAdapter()
    val extras = intent.extras
    if (extras != null) {
      val extrasList: MutableList<LaunchParamsExtra> = ArrayList()
      for (key in extras.keySet()) {
        extrasList.add(LaunchParamsExtra(key, extras.get(key).toString(),
          LaunchParamsExtraType.STRING))
      }
      extrasAdapter.setItems(extrasList, true)
    }
    // TODO handle null case
    listExtrasView.adapter = extrasAdapter
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.intent_analyzer, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_export -> {

        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}