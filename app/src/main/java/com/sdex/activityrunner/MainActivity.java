package com.sdex.activityrunner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sdex.activityrunner.service.AppLoaderIntentService;
import com.sdex.commons.BaseActivity;

public class MainActivity extends BaseActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    AppLoaderIntentService.enqueueWork(this, new Intent());

    MobileAds.initialize(getApplicationContext(),
      getString(R.string.ad_app_id));

    AdView adView = findViewById(R.id.ad_view);
    adView.loadAd(new Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
      .build());

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, new AppsListFragment())
        .commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    //getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
