package com.sdex.activityrunner

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdex.activityrunner.about.AboutActivity
import com.sdex.activityrunner.app.ActivitiesListActivity
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.dialog.ApplicationOptionsDialog
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityMainBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.extensions.setItemsVisibility
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.preferences.PreferencesBottomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences
    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ApplicationsListAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main, menu)
                    configureSearchView(menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_settings -> {
                            PreferencesBottomDialog().show(
                                supportFragmentManager,
                                PreferencesBottomDialog.TAG,
                            )
                            true
                        }

                        R.id.action_launch_intent -> {
                            IntentBuilderActivity.start(this@MainActivity, null)
                            true
                        }

                        R.id.action_about -> {
                            AboutActivity.start(this@MainActivity)
                            true
                        }

                        else -> false
                    }
                }
            },
        )

        adapter = ApplicationsListAdapter(this, DisplayConfig()).apply {
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

        binding.list.adapter = adapter
        binding.list.setHasFixedSize(true)

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle)
                .collect { state ->
                    adapter.updateDisplayConfig(state.displayConfig)
                    adapter.submitList(state.items) {
                        if (state.items.isNotEmpty()) {
                            binding.progress.hide()
                        }
                        if (shouldScrollToTop()) {
                            binding.list.scrollToPosition(0)
                        }
                    }
                }
        }

        lifecycleScope.launch {
            viewModel.isSyncing.flowWithLifecycle(lifecycle)
                .collect { isSyncing ->
                    binding.syncProgress.isVisible = isSyncing
                }
        }

        binding.progress.show()
    }

    override fun onResume() {
        super.onResume()

        viewModel.quickSync()
    }

    private fun shouldScrollToTop(): Boolean {
        // scroll to top when the filter dialog is shown
        return supportFragmentManager.findFragmentByTag(PreferencesBottomDialog.TAG) != null ||
            // scroll to top when the list already is at the top to display new items
            (binding.list.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
    }

    private fun configureSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        // expand the view to the full width: https://stackoverflow.com/a/34050959/2894324
        searchView.apply {
            maxWidth = Int.MAX_VALUE
            queryHint = getString(R.string.action_search_hint)
        }

        val searchQuery = viewModel.searchQuery.value
        if (!searchQuery.isNullOrEmpty()) {
            searchView.post { searchView.setQuery(searchQuery, false) }
            searchItem.expandActionView()
            menu.setItemsVisibility(exception = searchItem, visible = false)
        }

        searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.search(newText)
                    return false
                }
            },
        )
        searchItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    menu.setItemsVisibility(exception = item, visible = false)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    menu.setItemsVisibility(visible = true)
                    invalidateOptionsMenu()
                    return true
                }
            },
        )
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
