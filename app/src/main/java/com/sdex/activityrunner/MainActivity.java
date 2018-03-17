package com.sdex.activityrunner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.sdex.activityrunner.intent.LaunchParamsActivity;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;
import com.sdex.commons.ads.DisableAdsActivity;
import com.sdex.commons.util.UIUtils;

public class MainActivity extends BaseActivity {

  private AdsHandler adsHandler;
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
    adsHandler = new AdsHandler(this, adsContainer);
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
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_launch_intent: {
        LaunchParamsActivity.start(this, null);
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
}
