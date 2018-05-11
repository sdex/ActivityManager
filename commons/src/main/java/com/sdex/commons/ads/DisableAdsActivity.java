package com.sdex.commons.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.R;

public class DisableAdsActivity extends BaseActivity {

  public static final String ARG_ADS_ID = "arg_ads_id";

  public static final int REQUEST_CODE = 111;

  private RewardedVideoAd rewardedVideoAd;
  private Button btnWatch;
  private TextView ready;
  private TextView status;
  private ProgressBar progress;
  private AppPreferences appPreferences;
  private String adsId;

  public static Intent getStartIntent(Context context, @StringRes int adsId) {
    Intent starter = new Intent(context, DisableAdsActivity.class);
    starter.putExtra(ARG_ADS_ID, adsId);
    return starter;
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_disable_ads;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    enableBackButton();

    int adsIdRes = getIntent().getIntExtra(ARG_ADS_ID, 0);
    if (adsIdRes != 0) {
      adsId = getString(adsIdRes);
    }

    appPreferences = new AppPreferences(DisableAdsActivity.this);

    rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
    rewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);

    loadRewardedVideoAd();

    btnWatch = findViewById(R.id.watch);
    ready = findViewById(R.id.ready);
    status = findViewById(R.id.status);
    progress = findViewById(R.id.progress);

    updateAdsStatus();

    btnWatch.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (rewardedVideoAd.isLoaded()) {
          rewardedVideoAd.show();
        }
      }
    });
  }

  @Override
  public void onResume() {
    rewardedVideoAd.resume(this);
    super.onResume();
  }

  @Override
  public void onPause() {
    rewardedVideoAd.pause(this);
    super.onPause();
  }

  @Override
  public void onDestroy() {
    rewardedVideoAd.destroy(this);
    super.onDestroy();
  }

  private void updateAdsStatus() {
    if (appPreferences.isAdsActive()) {
      status.setText(R.string.commons_ads_active);
    } else {
      String adsDueTime = appPreferences.getAdsDueTime();
      status.setText(getString(R.string.commons_ads_disabled, adsDueTime));
    }
  }

  private void loadRewardedVideoAd() {
    String ads = adsId;
//    String ads = "ca-app-pub-3940256099942544/5224354917";
    rewardedVideoAd.loadAd(ads,
      new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .build());
  }

  private void toggleVideoReadiness(boolean isReady) {
    btnWatch.setEnabled(isReady);
    ready.setText(isReady ? R.string.commons_ads_ready : R.string.commons_ads_not_ready);
    progress.setVisibility(isReady ? View.GONE : View.VISIBLE);
  }

  private final RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
    @Override
    public void onRewardedVideoAdLoaded() {
      toggleVideoReadiness(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
      loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
      appPreferences.onVideoWatched();
      setResult(RESULT_OK);

      toggleVideoReadiness(false);
      updateAdsStatus();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
      loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
  };
}
