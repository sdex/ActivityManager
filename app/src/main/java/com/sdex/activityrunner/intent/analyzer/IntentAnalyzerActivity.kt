package com.sdex.activityrunner.intent.analyzer

import android.os.Bundle
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityIntentAnalyzerBinding
import com.sdex.activityrunner.extensions.getFlagsList
import com.sdex.activityrunner.intent.LaunchParamsExtra
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter
import com.sdex.activityrunner.intent.LaunchParamsExtraType
import com.sdex.activityrunner.intent.LaunchParamsListAdapter
import com.sdex.activityrunner.intent.param.None

class IntentAnalyzerActivity : BaseActivity() {

    private lateinit var binding: ActivityIntentAnalyzerBinding

    @Suppress("DEPRECATION")
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
                val value = extras.get(key)
                val safeValue = value?.toString() ?: ""
                extrasList.add(LaunchParamsExtra(key, safeValue, LaunchParamsExtraType.STRING))
            }
            extrasAdapter.setItems(extrasList, true)
        }
        binding.listExtrasView.adapter = extrasAdapter
    }
}
