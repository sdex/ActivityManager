package com.sdex.activityrunner

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.service.ApplicationsListJob
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.UIUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private val appPreferences by lazy { AppPreferences(this) }
    private val adapter by lazy { ApplicationsListAdapter(this) }

    override fun getLayout() = R.layout.activity_main

    public override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(appPreferences.theme)
        super.onCreate(savedInstanceState)

        ApplicationsListJob.enqueueWork(this, Intent())

        viewModel.items.observe(this) {
            adapter.submitList(it)
            progress.hide()
        }

        progress.show()

        list.addDivider(this)
        list.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        if (appPreferences.isShowSystemAppIndicator != adapter.isShowSystemAppIndicator) {
            adapter.isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        val hint = getString(R.string.action_search_hint)
        searchView.queryHint = hint

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
