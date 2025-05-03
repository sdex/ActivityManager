package com.sdex.activityrunner.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.dialog.ActivityOptionsDialog
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityActivitiesListBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.extensions.serializable
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.shortcut.AddShortcutDialogActivity
import com.sdex.activityrunner.util.UIUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActivitiesListActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private val viewModel by viewModels<ActivitiesListViewModel>()
    private lateinit var binding: ActivityActivitiesListBinding
    private lateinit var appPackageName: String

    private var searchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val item = intent.serializable<ApplicationModel>(ARG_APPLICATION)
        if (item == null && intent.data == null) {
            finish()
            return
        }
        title = item?.name
        appPackageName = item?.packageName ?: intent.data.toString()
        binding = ActivityActivitiesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        val adapter = ActivitiesListAdapter(this).apply {
            application = item
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

        viewModel.getItems(appPackageName, item).observe(this) { uiData ->
            if (uiData.application == null) {
                Toast.makeText(
                    this,
                    getString(R.string.activities_list_failed_loading, appPackageName),
                    Toast.LENGTH_LONG,
                ).show()
                finish()
                return@observe
            }

            title = uiData.application.name
            val totalActivitiesFormattedText = resources.getQuantityString(
                R.plurals.activities_count,
                uiData.application.activitiesCount,
                uiData.application.activitiesCount,
            )
            subTitle = getString(
                R.string.app_info_activities_number,
                totalActivitiesFormattedText,
                uiData.application.exportedActivitiesCount,
            )
            binding.empty.isVisible = (uiData.activities.isEmpty() && searchText == null)
            adapter.application = uiData.application
            adapter.submitList(uiData.activities) {
                binding.list.scrollToPosition(0)
            }
        }

        binding.showNonExported.setOnClickListener {
            viewModel.reloadItems(appPackageName, true)
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
                viewModel.reloadItems(appPackageName, item.isChecked)
                true
            }

            R.id.create_shortcut -> {
                val activityPackageName = packageName
                AddShortcutDialogActivity.start(
                    this,
                    HistoryModel().apply {
                        name = title?.toString()
                        packageName = activityPackageName
                        className = ActivitiesListActivity::class.java.name
                        data = appPackageName
                    },
                )
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

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filter(newText)
                    return false
                }
            },
        )
        searchItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
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
            },
        )
    }

    companion object {

        private const val ARG_APPLICATION = "arg_application"
        private const val STATE_SEARCH_TEXT = "state_search_text"

        fun start(context: Context, item: ApplicationModel) {
            context.startActivity(
                Intent(context, ActivitiesListActivity::class.java).apply {
                    putExtra(ARG_APPLICATION, item)
                },
            )
        }
    }
}
