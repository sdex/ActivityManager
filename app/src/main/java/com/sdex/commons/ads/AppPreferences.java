package com.sdex.commons.ads;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

  private static final String PREFERENCES_NAME = "ads_preferences";
  private static final String KEY_PRO = "pro";
  private static final String KEY_HISTORY_WARNING_SHOWN = "history_warning_shown";
  private static final String KEY_NOT_EXPORTED_DIALOG_SHOWN = "not_exported_dialog_shown";

  private final SharedPreferences preferences;

  public AppPreferences(Context context) {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
  }

  public void setProVersion(boolean isPro) {
    preferences.edit()
      .putBoolean(KEY_PRO, isPro)
      .apply();
  }

  public boolean isProVersion() {
    return preferences.getBoolean(KEY_PRO, false);
  }

  public SharedPreferences getPreferences() {
    return preferences;
  }

  public boolean isHistoryWarningShown() {
    return preferences.getBoolean(KEY_HISTORY_WARNING_SHOWN, false);
  }

  public void setHistoryWarningShown(boolean historyWarningShown) {
    preferences.edit()
      .putBoolean(KEY_HISTORY_WARNING_SHOWN, historyWarningShown)
      .apply();
  }

  public boolean isNotExportedDialogShown() {
    return preferences.getBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, false);
  }

  public void setNotExportedDialogShown(boolean notExportedDialogShown) {
    preferences.edit()
      .putBoolean(KEY_NOT_EXPORTED_DIALOG_SHOWN, notExportedDialogShown)
      .apply();
  }
}
