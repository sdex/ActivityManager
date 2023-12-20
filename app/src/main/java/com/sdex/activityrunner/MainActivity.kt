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
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sdex.activityrunner.about.AboutActivity
import com.sdex.activityrunner.app.ActivitiesListActivity
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.MainViewModel
import com.sdex.activityrunner.app.dialog.ApplicationOptionsDialog
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityMainBinding
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.util.AppUtils
import com.sdex.activityrunner.util.UIUtils
import dagger.hilt.android.AndroidEntryPoint
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

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
                configureSearchView(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_launch_intent -> {
                        IntentBuilderActivity.start(this@MainActivity, null)
                        true
                    }

                    R.id.action_about -> {
                        AboutActivity.start(this@MainActivity)
                        true
                    }

                    R.id.action_settings -> {
                        SettingsActivity.start(this@MainActivity)
                        true
                    }

                    else -> false
                }
            }
        })

        adapter = ApplicationsListAdapter(this, appPreferences).apply {
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

        viewModel.items.observe(this) {
            adapter.submitList(it) {
                if (it.isNotEmpty()) {
                    binding.progress.hide()
                }
            }
        }

        binding.progress.show()

        if (appPreferences.appOpenCounter % 10 == 0) {
            val behavior = BaseTransientBottomBar.Behavior().apply {
                setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
            }
            Snackbar.make(binding.coordinator, R.string.about_donation, Snackbar.LENGTH_INDEFINITE)
                .setBehavior(behavior)
                .setAction(R.string.donate_action_text) {
                    AppUtils.openLink(this, getString(R.string.donate_link))
                }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.update()
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
            UIUtils.setMenuItemsVisibility(menu, searchItem, false)
        }

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.search(newText)
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
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
