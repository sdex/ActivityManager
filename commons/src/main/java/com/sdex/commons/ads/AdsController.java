package com.sdex.commons.ads;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdsController {

  public static final long PERIOD = 60 * 60 * 1000; // 1 hour

  private static final String PREFERENCES_NAME = "ads_preferences";
  private static final String KEY_TIME = "ads_time";

  private static final SimpleDateFormat DATE_FORMAT =
    new SimpleDateFormat("MMM d HH:mm", Locale.ENGLISH);

  private final SharedPreferences preferences;

  public AdsController(Context context) {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
  }

  public void onVideoWatched() {
    long dueTo = (isAdsActive() ? now() : preferences.getLong(KEY_TIME, now()))
      + PERIOD;
    preferences.edit()
      .putLong(KEY_TIME, dueTo)
      .apply();
  }

  public String getAdsDueTime() {
    long time = preferences.getLong(KEY_TIME, 0);
    return DATE_FORMAT.format(new Date(time));
  }

  public boolean isAdsActive() {
    long now = now();
    return now >= preferences.getLong(KEY_TIME, now);
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
