package com.sdex.activityrunner.preferences;

import android.content.SharedPreferences;

import com.sdex.activityrunner.SettingsActivity;

public class AdvancedPreferences {

  private final SharedPreferences sharedPreferences;

  public AdvancedPreferences(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public boolean isShowNotExported() {
    return sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_NOT_EXPORTED,
      SettingsActivity.KEY_ADVANCED_NOT_EXPORTED_DEFAULT);
  }

  public boolean isRootIntegrationEnabled() {
    return sharedPreferences.getBoolean(SettingsActivity.KEY_ADVANCED_ROOT_INTEGRATION,
      SettingsActivity.KEY_ADVANCED_ROOT_INTEGRATION_DEFAULT);
  }
}
