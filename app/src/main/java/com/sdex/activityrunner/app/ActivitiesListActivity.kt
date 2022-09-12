package com.sdex.activityrunner.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ActivityOptionsDialog
import com.sdex.activityrunner.databinding.ActivityActivitiesListBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.addDividerItemDecoration
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.UIUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitiesListActivity : BaseActivity() {

    private val viewModel by viewModels<ActivitiesListViewModel>()
    private val appPreferences by lazy { AppPreferences(this) }
    private lateinit var binding: ActivityActivitiesListBinding
    private lateinit var appPackageName: String

    private var showNotExported: Boolean = false
    private var searchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.serializable<ApplicationModel>(ARG_APPLICATION)
        if (item == null) {
            finish()
            return
        }
        binding = ActivityActivitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        appPackageName = item.packageName
        title = item.name
        binding.list.addDividerItemDecoration()
        val adapter = ActivitiesListAdapter(this, item).apply {
            itemClickListener = object : ActivitiesListAdapter.ItemClickListener {
                override fun onItemClick(item: ActivityModel) {
                    launchActivity(item)
                }

                override fun onItemLongClick(item: ActivityModel) {
                    val dialog = ActivityOptionsDialog.newInstance(item)
                    dialog.show(supportFragmentManager, ActivityOptionsDialog.TAG)
                }
            }
        }
        binding.list.adapter = adapter

        searchText = savedInstanceState?.getString(STATE_SEARCH_TEXT)

        if (item.enabled) {
            viewModel.getItems(appPackageName).observe(this) {
                adapter.submitList(it)
                val size = it.size
                setSubtitle(resources.getQuantityString(R.plurals.activities_count, size, size))
                binding.empty.isVisible = (size == 0 && searchText == null)
            }
        } else {
            binding.disabled.isVisible = true
        }

        showNotExported = appPreferences.showNotExported

        binding.turnOnAdvanced.setOnClickListener {
            appPreferences.showNotExported = true
            viewModel.reloadItems(appPackageName)
        }

        binding.openAppInfo.setOnClickListener {
            IntentUtils.openApplicationInfo(this, appPackageName)
        }

        if (!appPreferences.showNotExported && !appPreferences.isNotExportedDialogShown) {
            appPreferences.isNotExportedDialogShown = true
            val dialog = EnableNotExportedActivitiesDialog()
            dialog.show(supportFragmentManager, EnableNotExportedActivitiesDialog.TAG)
        }
    }

    override fun onStart() {
        super.onStart()
        if (appPreferences.showNotExported != showNotExported) {
            viewModel.reloadItems(appPackageName)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SEARCH_TEXT, searchText)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activities_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        // expand the view to the full width: https://stackoverflow.com/a/34050959/2894324
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.action_search_activity_hint)

        if (searchText != null) {
            searchView.post { searchView.setQuery(searchText, false) }
            searchItem.expandActionView()
            UIUtils.setMenuItemsVisibility(menu, searchItem, false)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                UIUtils.setMenuItemsVisibility(menu, item, false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchText = null
                UIUtils.setMenuItemsVisibility(menu, true)
                invalidateOptionsMenu()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filter(text: String?) {
        this.searchText = text
        viewModel.filterItems(searchText)
    }

    companion object {

        private const val ARG_APPLICATION = "arg_application"
        private const val STATE_SEARCH_TEXT = "state_search_text"

        fun start(context: Context, item: ApplicationModel) {
            context.startActivity(Intent(context, ActivitiesListActivity::class.java).apply {
                putExtra(ARG_APPLICATION, item)
            })
        }
    }
}
