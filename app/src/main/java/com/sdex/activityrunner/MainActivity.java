package com.sdex.activityrunner;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.sdex.activityrunner.intent.IntentBuilderActivity;
import com.sdex.activityrunner.preferences.SettingsActivity;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.activityrunner.util.RecyclerViewHelper;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsDelegate;
import com.sdex.commons.ads.AppPreferences;
import com.sdex.commons.util.AppUtils;
import com.sdex.commons.util.UIUtils;

import java.util.List;

public class MainActivity extends BaseActivity {

  private static final String STATE_SEARCH_TEXT = "state_search_text";

  private AdsDelegate adsDelegate;
  private AppPreferences appPreferences;
  private boolean isProVersionEnabled;
  private ApplicationsListAdapter adapter;
  private SwipeRefreshLayout refreshLayout;
  private ContentLoadingProgressBar progressBar;
  private ApplicationListViewModel viewModel;
  private String searchText;

  @Override
  protected int getLayout() {
    return R.layout.activity_main;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppLoaderIntentService.enqueueWork(this, new Intent());

    viewModel = ViewModelProviders.of(this).get(ApplicationListViewModel.class);

    appPreferences = new AppPreferences(this);
    adsDelegate = new AdsDelegate(appPreferences);
    adsDelegate.initInterstitialAd(this, R.string.ad_interstitial_unit_id);

    fetchPurchases();
    showRatingDialog();

    if (savedInstanceState != null) {
      searchText = savedInstanceState.getString(STATE_SEARCH_TEXT);
    }

    progressBar = findViewById(R.id.progress);
    progressBar.show();
    refreshLayout = findViewById(R.id.refresh);
    RecyclerView list = findViewById(R.id.list);
    RecyclerViewHelper.addDivider(list);
    adapter = new ApplicationsListAdapter(this);
    list.setAdapter(adapter);
    refreshLayout.setOnRefreshListener(() -> {
      refreshLayout.setRefreshing(true);
      final Intent work = new Intent();
      work.putExtra(AppLoaderIntentService.ARG_REASON, AppLoaderIntentService.REFRESH_USER);
      AppLoaderIntentService.enqueueWork(this, work);
    });

    checkOreoBug();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(STATE_SEARCH_TEXT, searchText);
  }

  @Override
  public void onResume() {
    super.onResume();
    viewModel.getItems(searchText).observe(this, itemModels -> {
      if (itemModels != null) {
        adapter.submitList(itemModels);
        refreshLayout.setRefreshing(false);
        progressBar.hide();
      }
    });
  }

  private void filter(String text) {
    if (adapter != null) {
      this.searchText = text;
      viewModel.getItems(text).observe(this,
        itemModels -> adapter.submitList(itemModels));
    }
  }

  // https://issuetracker.google.com/issues/73289329
  private void checkOreoBug() {
    if (VERSION.SDK_INT == VERSION_CODES.O) {
      boolean warningWasShown = appPreferences.getPreferences().getBoolean(
        OreoPackageManagerBugActivity.KEY, false);
      if (!warningWasShown) {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getPackages().observe(this, packageInfo -> {
          if (packageInfo != null) {
            if (packageInfo.isEmpty()) {
              overridePendingTransition(0, 0);
              startActivity(new Intent(this, OreoPackageManagerBugActivity.class));
            }
          }
        });
      }
    }
  }

  private void fetchPurchases() {
    BillingClient billingClient = BillingClient.newBuilder(this)
      .setListener((responseCode, purchases) -> {
        if (responseCode == BillingResponse.OK) {
          handlePurchases(purchases);
        }
      })
      .build();
    billingClient.startConnection(new BillingClientStateListener() {
      @Override
      public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingResponse.OK) {
          PurchasesResult purchasesResult = billingClient.queryPurchases(SkuType.INAPP);
          List<Purchase> purchases = purchasesResult.getPurchasesList();
          handlePurchases(purchases);
        }
      }

      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    });
  }

  private void handlePurchases(@Nullable List<Purchase> purchases) {
    if (purchases != null) {
      for (Purchase purchase : purchases) {
        if (PurchaseActivity.isPremiumVersion(purchase.getSku())) {
          isProVersionEnabled = true;
          invalidateOptionsMenu();
          break;
        }
      }
    }
    appPreferences.setProVersion(isProVersionEnabled);
  }

  private void showRatingDialog() {
    final RatingDialog ratingDialog = new RatingDialog.Builder(this)
      .threshold(3)
      .session(15)
      .onRatingBarFormSumbit(feedback ->
        AppUtils.sendEmail(this, AppUtils.ACTIVITY_RUNNER_FEEDBACK_EMAIL,
          AppUtils.ACTIVITY_RUNNER_FEEDBACK_SUBJECT, feedback))
      .build();
    ratingDialog.show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    String hint = getString(R.string.action_search_hint);
    searchView.setQueryHint(hint);

    if (!TextUtils.isEmpty(searchText)) {
      searchView.post(() -> searchView.setQuery(searchText, false));
      searchItem.expandActionView();
      UIUtils.setMenuItemsVisibility(menu, searchItem, false);
    }

    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filter(newText);
        return false;
      }
    });
    searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item) {
        UIUtils.setMenuItemsVisibility(menu, item, false);
        return true;
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item) {
        UIUtils.setMenuItemsVisibility(menu, true);
        invalidateOptionsMenu();
        return true;
      }
    });
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (isProVersionEnabled) {
      menu.findItem(R.id.action_upgrade).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_launch_intent: {
        IntentBuilderActivity.start(this, null);
        return true;
      }
      case R.id.action_upgrade: {
        PurchaseActivity.start(this);
        return true;
      }
      case R.id.action_about: {
        AboutActivity.start(this);
        return true;
      }
      case R.id.action_settings: {
        SettingsActivity.start(this, SettingsActivity.NORMAL);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    adsDelegate.showInterstitial();
  }
}
