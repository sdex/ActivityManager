package com.sdex.activityrunner;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.sdex.activityrunner.intent.LaunchParamsActivity;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;
import com.sdex.commons.ads.AppPreferences;
import com.sdex.commons.ads.DisableAdsActivity;
import com.sdex.commons.util.AppUtils;
import com.sdex.commons.util.UIUtils;
import java.util.List;

public class MainActivity extends BaseActivity {

  private AdsHandler adsHandler;
  private AppPreferences appPreferences;
  private BillingClient billingClient;
  private boolean isProVersionEnabled;

  private AppsListFragment appsListFragment;

  @Override
  protected int getLayout() {
    return R.layout.activity_main;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppLoaderIntentService.enqueueWork(this, new Intent());

    FrameLayout adsContainer = findViewById(R.id.ads_container);
    appPreferences = new AppPreferences(this);
    adsHandler = new AdsHandler(appPreferences, adsContainer);
    adsHandler.init(this, R.string.ad_banner_unit_id);

    if (savedInstanceState == null) {
      appsListFragment = new AppsListFragment();
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, appsListFragment, AppsListFragment.TAG)
        .commit();
    } else {
      appsListFragment = (AppsListFragment) getSupportFragmentManager()
        .findFragmentByTag(AppsListFragment.TAG);
    }
    fetchPurchases();
    showRatingDialog();

    checkOreoBug();
  }

  // https://issuetracker.google.com/issues/73289329
  private void checkOreoBug() {
    if (VERSION.SDK_INT == VERSION_CODES.O) {
      MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
      viewModel.getPackages().observe(this, packageInfo -> {
        if (packageInfo != null) {
          if (packageInfo.isEmpty()) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, OreoPackageManagerBugActivity.class));
          }
        }
      });
    }
  }

  private void fetchPurchases() {
    billingClient = BillingClient.newBuilder(this)
      .setListener((responseCode, purchases) -> {
        if (responseCode == BillingResponse.OK && purchases != null) {
          handlePurchases(purchases);
        } else if (responseCode == BillingResponse.USER_CANCELED) {
          // Handle an error caused by a user cancelling the purchase flow.
        } else {
          // Handle any other error codes.
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

  private void handlePurchases(List<Purchase> purchases) {
    for (Purchase purchase : purchases) {
      if (PurchaseActivity.SKU_PRO.equals(purchase.getSku())) {
        isProVersionEnabled = true;
        invalidateOptionsMenu();
      }
    }

    appPreferences.setProVersion(isProVersionEnabled);
    adsHandler.detachBottomBannerIfNeed();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    String hint = getString(R.string.action_search_hint);
    searchView.setQueryHint(hint);
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (appsListFragment != null) {
          appsListFragment.filter(newText);
        }
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
      menu.findItem(R.id.action_disable_ads).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_launch_intent: {
        LaunchParamsActivity.start(this, null);
        return true;
      }
      case R.id.action_upgrade: {
        PurchaseActivity.start(this);
        return true;
      }
      case R.id.action_disable_ads: {
        Intent intent = DisableAdsActivity.getStartIntent(this, R.string.ad_rewarded_unit_id);
        startActivityForResult(intent, DisableAdsActivity.REQUEST_CODE);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    adsHandler.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onResume() {
    super.onResume();
    adsHandler.detachBottomBannerIfNeed();
  }
}
