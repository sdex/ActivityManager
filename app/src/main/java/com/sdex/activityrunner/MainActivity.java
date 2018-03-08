package com.sdex.activityrunner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sdex.commons.BaseActivity;

public class MainActivity extends BaseActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MobileAds.initialize(getApplicationContext(),
      getString(R.string.ad_app_id));

    AdView adView = findViewById(R.id.ad_view);
    Builder bannerAdBuilder = new Builder();
    if (BuildConfig.DEBUG) {
      bannerAdBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
    }
    adView.loadAd(bannerAdBuilder.build());

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, new AllTasksListFragment())
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
      case R.id.action_about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
