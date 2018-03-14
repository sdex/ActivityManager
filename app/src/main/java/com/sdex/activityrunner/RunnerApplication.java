package com.sdex.activityrunner;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;

public class RunnerApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Crashlytics crashlyticsKit = new Crashlytics.Builder()
      .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      .build();
    Fabric.with(this, crashlyticsKit);
  }
}
