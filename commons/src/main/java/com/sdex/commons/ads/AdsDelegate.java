package com.sdex.commons.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;

import com.google.ads.mediation.admob.AdMobAdapter;
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
  private Bundle adRequestExtras;

  public AdsDelegate(AppPreferences appPreferences) {
    this(appPreferences, null);
  }

  public AdsDelegate(AppPreferences appPreferences, @Nullable FrameLayout bannerContainer) {
    this.appPreferences = appPreferences;
    this.bannerContainer = bannerContainer;

    adRequestExtras = new Bundle(1);
    adRequestExtras.putString("npa", appPreferences.isAdsPersonalized() ? "0" : "1");
  }

  public void initBanner(Context context, @StringRes int adBannerUnitId) {
    if (appPreferences.isAdsActive() && bannerContainer != null) {
      final AdView adView = new AdView(context);
      adView.setAdUnitId(context.getString(adBannerUnitId));
      adView.setAdSize(AdSize.SMART_BANNER);
      final AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addNetworkExtrasBundle(AdMobAdapter.class, adRequestExtras)
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
      final AdRequest adRequest = new AdRequest.Builder()
        .addNetworkExtrasBundle(AdMobAdapter.class, adRequestExtras)
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build();
      interstitialAd.loadAd(adRequest);
    }
  }

  public void detachBottomBannerIfNeed() {
    detachBottomBannerIfNeed(!appPreferences.isAdsActive());
  }

  private void detachBottomBannerIfNeed(boolean detach) {
    if (detach && bannerContainer != null) {
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
    if (appPreferences.isInterstitialAdActive() && interstitialAd != null
      && interstitialAd.isLoaded()) {
      appPreferences.onInterstitialAdShown();
      interstitialAd.show();
    }
  }

}
