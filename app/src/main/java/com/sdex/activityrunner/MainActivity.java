package com.sdex.activityrunner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsController;
import com.sdex.commons.ads.DisableAdsActivity;

public class MainActivity extends BaseActivity {

  private AdsController adsController;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    adsController = new AdsController(this);

    AppLoaderIntentService.enqueueWork(this, new Intent());

    FrameLayout adsContainer = findViewById(R.id.ads_container);

    if (adsController.isAdsActive() && adsContainer.getChildCount() == 0) {
      MobileAds.initialize(getApplicationContext(),
        getString(R.string.ad_app_id));

      AdView adView = new AdView(this);
      adView.setAdUnitId(getString(R.string.ad_banner_unit_id));
      adView.setAdSize(AdSize.SMART_BANNER);
      adsContainer.addView(adView);

      AdRequest adRequest = new Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
      adView.loadAd(adRequest);
    }

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, new AppsListFragment())
        .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about: {
        startActivity(new Intent(this, AboutActivity.class));
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
    if (requestCode == DisableAdsActivity.REQUEST_CODE && resultCode == RESULT_OK) {
      if (!adsController.isAdsActive()) {
        hideBottomAds();
      }
    }
  }

  private void hideBottomAds() {
    FrameLayout adsContainer = findViewById(R.id.ads_container);
    adsContainer.removeAllViews();
  }
}
