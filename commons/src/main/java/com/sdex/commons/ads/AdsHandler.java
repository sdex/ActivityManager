package com.sdex.commons.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdsHandler {

  private AppPreferences appPreferences;
  private FrameLayout container;

  public AdsHandler(Context context, FrameLayout container) {
    this.appPreferences = new AppPreferences(context);
    this.container = container;
  }

  public AdsHandler(AppPreferences appPreferences, FrameLayout container) {
    this.appPreferences = appPreferences;
    this.container = container;
  }

  public void init(Context context, @StringRes int adBannerUnitId) {
    if (appPreferences.isAdsActive()) {
      final AdView adView = new AdView(context);
      adView.setAdUnitId(context.getString(adBannerUnitId));
      adView.setAdSize(AdSize.SMART_BANNER);
      AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
      adView.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
          super.onAdLoaded();
          container.removeAllViews();
          container.addView(adView);
        }
      });
      adView.loadAd(adRequest);
    }
  }

  public void detachBottomBannerIfNeed() {
    if (!appPreferences.isAdsActive()) {
      container.removeAllViews();
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == DisableAdsActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      detachBottomBannerIfNeed();
    }
  }
}
