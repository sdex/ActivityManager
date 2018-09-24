package com.sdex.activityrunner

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.Purchase
import com.codemybrainsout.ratingdialog.RatingDialog
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.app.ApplicationsListViewModel
import com.sdex.activityrunner.app.legacy.OreoPackageManagerBugActivity
import com.sdex.activityrunner.consent.ConsentHelper
import com.sdex.activityrunner.extensions.addDivider
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AdvancedPreferences
import com.sdex.activityrunner.preferences.SettingsActivity
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.activityrunner.service.ApplicationsListWorker
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AdsDelegate
import com.sdex.commons.ads.AppPreferences
import com.sdex.commons.util.AppUtils
import com.sdex.commons.util.UIUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

  private val appPreferences: AppPreferences by lazy { AppPreferences(this) }
  private val adsDelegate: AdsDelegate by lazy { AdsDelegate(appPreferences) }
  private val advancedPreferences: AdvancedPreferences by lazy {
    AdvancedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
  }
  private val adapter: ApplicationsListAdapter by lazy { ApplicationsListAdapter(this) }
  private val viewModel: ApplicationsListViewModel by lazy {
    ViewModelProviders.of(this).get(ApplicationsListViewModel::class.java)
  }

  private var isProVersionEnabled: Boolean = false
  private var isShowSystemAppIndicator: Boolean = false
  private var searchText: String? = null

  override fun getLayout(): Int {
    return R.layout.activity_main
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val request = OneTimeWorkRequest.Builder(ApplicationsListWorker::class.java)
      .addTag(ApplicationsListWorker.TAG)
      .build()
    WorkManager.getInstance().enqueue(request)

    adsDelegate.initInterstitialAd(this, R.string.ad_interstitial_unit_id)

    isShowSystemAppIndicator = advancedPreferences.isShowSystemAppIndicator

    fetchPurchases()
    showRatingDialog()

    searchText = savedInstanceState?.getString(STATE_SEARCH_TEXT)

    viewModel.getItems(searchText).observe(this, Observer {
      adapter.submitList(it)
      progress.hide()
    })

    progress.show()

    list.addDivider()
    list.adapter = adapter

    checkOreoBug()
  }

  override fun onStart() {
    super.onStart()
    if (advancedPreferences.isShowSystemAppIndicator != isShowSystemAppIndicator) {
      isShowSystemAppIndicator = advancedPreferences.isShowSystemAppIndicator
      viewModel.getItems(searchText).observe(this, Observer {
        adapter.submitList(it)
        adapter.notifyDataSetChanged()
      })
    }
  }

  public override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(STATE_SEARCH_TEXT, searchText)
  }

  private fun filter(text: String) {
    this.searchText = text
    viewModel.getItems(text).observe(this, Observer {
      adapter.submitList(it)
    })
  }

  // https://issuetracker.google.com/issues/73289329
  private fun checkOreoBug() {
    if (VERSION.SDK_INT == VERSION_CODES.O) {
      val warningWasShown = appPreferences.preferences.getBoolean(
        OreoPackageManagerBugActivity.KEY, false)
      if (!warningWasShown) {
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.packages.observe(this, Observer {
          if (it!!.isEmpty()) {
            overridePendingTransition(0, 0)
            startActivity(Intent(this, OreoPackageManagerBugActivity::class.java))
          }
        })
      }
    }
  }

  private fun fetchPurchases() {
    val billingClient = BillingClient.newBuilder(this)
      .setListener { responseCode, purchases ->
        if (responseCode == BillingResponse.OK) {
          handlePurchases(purchases)
        }
      }
      .build()
    billingClient.startConnection(object : BillingClientStateListener {
      override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
        if (billingResponseCode == BillingResponse.OK) {
          val purchasesResult = billingClient.queryPurchases(SkuType.INAPP)
          val purchases = purchasesResult.purchasesList
          handlePurchases(purchases)
        }
      }

      override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    })
  }

  private fun handlePurchases(purchases: List<Purchase>?) {
    isProVersionEnabled = false
    if (purchases != null) {
      for (purchase in purchases) {
        if (PurchaseActivity.isPremiumVersion(purchase.sku)) {
          isProVersionEnabled = true
          break
        }
      }
    }
    invalidateOptionsMenu()
    appPreferences.isProVersion = isProVersionEnabled

    if (!isProVersionEnabled) {
      val consentHelper = ConsentHelper()
      consentHelper.handleConsent(this)
    }
  }

  private fun showRatingDialog() {
    val threshold = 3f
    val sessions = 10
    val ratingDialog = RatingDialog.Builder(this)
      .threshold(threshold)
      .session(sessions)
      .onRatingBarFormSumbit { feedback ->
        AppUtils.sendEmail(this, AppUtils.ACTIVITY_RUNNER_FEEDBACK_EMAIL,
          AppUtils.ACTIVITY_RUNNER_FEEDBACK_SUBJECT, feedback)
      }
      .build()
    ratingDialog.show()
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
        UIUtils.setMenuItemsVisibility(menu, true)
        invalidateOptionsMenu()
        return true
      }
    })
    return super.onCreateOptionsMenu(menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    if (isProVersionEnabled) {
      val itemUpgrade = menu.findItem(R.id.action_upgrade)
      itemUpgrade?.isVisible = false
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_launch_intent -> {
        IntentBuilderActivity.start(this, null)
        true
      }
      R.id.action_upgrade -> {
        PurchaseActivity.start(this)
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

  override fun onBackPressed() {
    super.onBackPressed()
    adsDelegate.showInterstitial()
  }

  companion object {

    private const val STATE_SEARCH_TEXT = "state_search_text"

    init {
      AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
  }
}
