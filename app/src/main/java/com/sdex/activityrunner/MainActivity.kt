package com.sdex.activityrunner

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.sdex.activityrunner.app.ActivitiesListActivity
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.dialog.ApplicationOptionsDialog
import com.sdex.activityrunner.databinding.ActivityMainBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.addDividerItemDecoration
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.service.ApplicationsListJob
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.UIUtils

class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val appPreferences by lazy { AppPreferences(this) }
    private lateinit var adapter: ApplicationsListAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(appPreferences.theme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        ApplicationsListJob.enqueueWork(this, Intent())

        adapter = ApplicationsListAdapter(this).apply {
            isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator
            itemClickListener = object : ApplicationsListAdapter.ItemClickListener {
                override fun onItemClick(item: ApplicationModel) {
                    ActivitiesListActivity.start(this@MainActivity, item)
                }

                override fun onItemLongClick(item: ApplicationModel) {
                    val dialog = ApplicationOptionsDialog.newInstance(item)
                    dialog.show(supportFragmentManager, ApplicationOptionsDialog.TAG)
                }
            }
        }

        binding.list.addDividerItemDecoration()
        binding.list.adapter = adapter

        viewModel.items.observe(this) {
            adapter.submitList(it) {
                if (it.isNotEmpty()) {
                    binding.progress.hide()
                }
            }
        }

        binding.progress.show()
    }

    override fun onStart() {
        super.onStart()
        if (appPreferences.isShowSystemAppIndicator != adapter.isShowSystemAppIndicator) {
            adapter.isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        // expand the view to the full width: https://stackoverflow.com/a/34050959/2894324
        searchView.maxWidth = Int.MAX_VALUE
        searchView.queryHint = getString(R.string.action_search_hint)

        val searchQuery = viewModel.searchQuery.value
        if (!searchQuery.isNullOrEmpty()) {
            searchView.post { searchView.setQuery(searchQuery, false) }
            searchItem.expandActionView()
            UIUtils.setMenuItemsVisibility(menu, searchItem, false)
        }

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchQuery.value = newText
                return false
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                UIUtils.setMenuItemsVisibility(menu, item, false)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                UIUtils.setMenuItemsVisibility(menu, true)
                invalidateOptionsMenu()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_launch_intent -> {
                IntentBuilderActivity.start(this, null)
                true
            }
            R.id.action_about -> {
                AboutActivity.start(this)
                true
            }
            R.id.action_settings -> {
                SettingsActivity.start(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
