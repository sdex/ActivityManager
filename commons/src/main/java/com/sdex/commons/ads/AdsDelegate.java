package com.sdex.commons.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AdsDelegate {

  private AppPreferences appPreferences;
  @Nullable
  private FrameLayout bannerContainer;
  private InterstitialAd interstitialAd;

  public AdsDelegate(AppPreferences appPreferences, @Nullable FrameLayout bannerContainer) {
    this.appPreferences = appPreferences;
    this.bannerContainer = bannerContainer;
  }

  public void initBanner(Context context, @StringRes int adBannerUnitId) {
    if (appPreferences.isAdsActive() && bannerContainer != null) {
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
          bannerContainer.removeAllViews();
          bannerContainer.addView(adView);
        }
      });
      if (adView.getAdSize() != null && adView.getAdUnitId() != null) {
        adView.loadAd(adRequest);
      }
    }
  }

  public void initInterstitialAd(Context context, @StringRes int adInterstitialUnitId) {
    if (appPreferences.isInterstitialAdActive()) {
      interstitialAd = new InterstitialAd(context);
      interstitialAd.setAdUnitId(context.getString(adInterstitialUnitId));
      AdRequest.Builder interstitialAdBuilder = new AdRequest.Builder();
      interstitialAdBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
      interstitialAd.loadAd(interstitialAdBuilder.build());
    }
  }

  public void detachBottomBannerIfNeed() {
    if (!appPreferences.isAdsActive() && bannerContainer != null) {
      bannerContainer.removeAllViews();
    }
  }

  @SuppressWarnings("unused")
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == DisableAdsActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      detachBottomBannerIfNeed();
    }
  }

  public void showInterstitial() {
    if (interstitialAd != null) {
      if (interstitialAd.isLoaded()) {
        appPreferences.onInterstitialAdShown();
        interstitialAd.show();
      }
    }
  }
}
