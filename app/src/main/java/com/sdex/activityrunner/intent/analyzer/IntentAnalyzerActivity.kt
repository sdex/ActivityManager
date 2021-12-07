package com.sdex.activityrunner.intent.analyzer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sdex.activityrunner.R
import com.sdex.activityrunner.databinding.ActivityIntentAnalyzerBinding
import com.sdex.activityrunner.extensions.getFlagsList
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.LaunchParamsListAdapter
import com.sdex.activityrunner.intent.param.None
import com.sdex.commons.BaseActivity

class IntentAnalyzerActivity : BaseActivity() {

    private lateinit var binding: ActivityIntentAnalyzerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntentAnalyzerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        binding.actionView.text = intent.action ?: None.VALUE
        binding.dataView.text = intent.dataString ?: None.VALUE
        binding.mimeTypeView.text = intent.type ?: None.VALUE

        val categoriesAdapter = LaunchParamsListAdapter()
        categoriesAdapter.setItems(intent.categories)
        binding.listCategoriesView.adapter = categoriesAdapter

        val flagsAdapter = LaunchParamsListAdapter()
        flagsAdapter.setItems(intent.getFlagsList())
        binding.listFlagsView.adapter = flagsAdapter

        val extrasAdapter = LaunchParamsExtraListAdapter()
        val extras = intent.extras
        if (extras != null) {
            val extrasList: MutableList<LaunchParamsExtra> = ArrayList()
            for (key in extras.keySet()) {
                val value = if (extras.get(key) != null) {
                    extras.get(key)!!.toString()
                } else {
                    ""
                }
                extrasList.add(LaunchParamsExtra(key, value, LaunchParamsExtraType.STRING))
            }
            extrasAdapter.setItems(extrasList, true)
        }
        // TODO handle null case
        binding.listExtrasView.adapter = extrasAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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