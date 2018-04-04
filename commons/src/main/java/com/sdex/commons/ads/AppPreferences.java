package com.sdex.commons.ads;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppPreferences {

  public static final long ADS_PERIOD = 60 * 60 * 1000; // 1 hour
  public static final long ADS_INTERSTITIAL_PERIOD = 60 * 60 * 1000; // 1 hour

  private static final String PREFERENCES_NAME = "ads_preferences";
  private static final String KEY_TIME_INTERSTITIAL = "ads_time_interstitial";
  private static final String KEY_TIME = "ads_time";
  private static final String KEY_PRO = "pro";

  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("MMM d HH:mm", Locale.ENGLISH);

  private final SharedPreferences preferences;

  public AppPreferences(Context context) {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
  }

  public void onVideoWatched() {
    long dueTo = (isAdsActive() ? now() : preferences.getLong(KEY_TIME, now()))
      + ADS_PERIOD;
    preferences.edit()
      .putLong(KEY_TIME, dueTo)
      .apply();
  }

  public void onInterstitialAdShown() {
    preferences.edit()
      .putLong(KEY_TIME_INTERSTITIAL, now())
      .apply();
  }

  public boolean isInterstitialAdActive() {
    if (isAdsActive()) {
      long now = now();
      long lastTimeAdShown = preferences.getLong(KEY_TIME_INTERSTITIAL, 0);
      return (now - lastTimeAdShown > ADS_INTERSTITIAL_PERIOD);
    }
    return false;
  }

  public String getAdsDueTime() {
    long time = preferences.getLong(KEY_TIME, 0);
    return DATE_FORMAT.format(new Date(time));
  }

  public boolean isAdsActive() {
    long now = now();
    return (now >= preferences.getLong(KEY_TIME, now)) && !isProVersion();
  }

  public void setProVersion(boolean isPro) {
    preferences.edit()
      .putBoolean(KEY_PRO, isPro)
      .apply();
  }

  public boolean isProVersion() {
    return preferences.getBoolean(KEY_PRO, false);
  }

  private static long now() {
    return System.currentTimeMillis();
  }

  public SharedPreferences getPreferences() {
    return preferences;
  }
}
