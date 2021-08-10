package com.sdex.activityrunner

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.ApplicationsListViewModel
import com.sdex.activityrunner.app.legacy.OreoPackageManagerBugActivity
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.service.ApplicationsListJob
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.UIUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val viewModel by viewModels<ApplicationsListViewModel>()

    private val appPreferences by lazy { AppPreferences(this) }
    private val adapter by lazy { ApplicationsListAdapter(this) }

    private var isShowSystemAppIndicator: Boolean = false
    private var searchText: String? = null

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(appPreferences.theme)
        super.onCreate(savedInstanceState)

        ApplicationsListJob.enqueueWork(this, Intent())

        isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator

        searchText = savedInstanceState?.getString(STATE_SEARCH_TEXT)

        viewModel.getItems(searchText).observe(this) {
            adapter.submitList(it)
            progress.hide()
        }

        progress.show()

        list.addDivider(this)
        list.adapter = adapter

        checkOreoBug()
    }

    override fun onStart() {
        super.onStart()
        if (appPreferences.isShowSystemAppIndicator != isShowSystemAppIndicator) {
            isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator
            viewModel.getItems(searchText).observe(this) {
                adapter.submitList(it)
            }
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SEARCH_TEXT, searchText)
    }

    private fun filter(text: String) {
        this.searchText = text
        viewModel.getItems(text).observe(this) {
            adapter.submitList(it)
        }
    }

    // https://issuetracker.google.com/issues/73289329
    private fun checkOreoBug() {
        if (VERSION.SDK_INT == VERSION_CODES.O) {
            if (!appPreferences.isOreoBugWarningShown) {
                val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
                viewModel.packages.observe(this) {
                    if (it!!.isEmpty()) {
                        overridePendingTransition(0, 0)
                        startActivity(Intent(this, OreoPackageManagerBugActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        val hint = getString(R.string.action_search_hint)
        searchView.queryHint = hint

        if (!TextUtils.isEmpty(searchText)) {
            searchView.post { searchView.setQuery(searchText, false) }
            searchItem.expandActionView()
            UIUtils.setMenuItemsVisibility(menu, searchItem, false)
        }

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
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

        private const val STATE_SEARCH_TEXT = "state_search_text"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
