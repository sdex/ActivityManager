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
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityActivitiesListBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.UIUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActivitiesListActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private val viewModel by viewModels<ActivitiesListViewModel>()
    private lateinit var binding: ActivityActivitiesListBinding
    private lateinit var app: ApplicationModel

    private var searchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.serializable<ApplicationModel>(ARG_APPLICATION)
        if (item == null) {
            finish()
            return
        }
        app = item
        binding = ActivityActivitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)
        title = app.name

        val adapter = ActivitiesListAdapter(this, app).apply {
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

        viewModel.getItems(app.packageName).observe(this) {
            adapter.submitList(it)
            val size = it.size
            setSubtitle(resources.getQuantityString(R.plurals.activities_count, size, size))
            binding.empty.isVisible = (size == 0 && searchText == null)
        }

        binding.showNonExported.setOnClickListener {
            viewModel.reloadItems(app.packageName, true)
        }

        if (!appPreferences.showNotExported
            && !appPreferences.isNotExportedDialogShown
            && appPreferences.appOpenCounter > 3
        ) {
            appPreferences.isNotExportedDialogShown = true
            val dialog = EnableNotExportedActivitiesDialog()
            dialog.show(supportFragmentManager, EnableNotExportedActivitiesDialog.TAG)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SEARCH_TEXT, searchText)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activities_list, menu)
        configureSearchView(menu)
        menu.findItem(R.id.show_not_exported).isChecked = appPreferences.showNotExported
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_not_exported -> {
                item.isChecked = !item.isChecked
                appPreferences.showNotExported = item.isChecked
                viewModel.reloadItems(app.packageName, item.isChecked)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filter(text: String?) {
        this.searchText = text
        viewModel.filterItems(searchText)
    }

    private fun configureSearchView(menu: Menu) {
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
