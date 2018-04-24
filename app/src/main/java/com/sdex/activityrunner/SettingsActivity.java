package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.sdex.activityrunner.util.CheckRootTask;
import com.sdex.commons.ads.AppPreferences;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

  public static final String KEY_SORT_ORDER = "sort_order";
  public static final String KEY_SORT_ORDER_DEFAULT = "0";
  public static final String KEY_SORT_BY = "sort_by";
  public static final String KEY_SORT_BY_DEFAULT = "0";
  public static final String KEY_SORT_CASE_SENSITIVE = "sort_case_sensitive";
  public static final boolean KEY_SORT_CASE_SENSITIVE_DEFAULT = true;
  public static final String KEY_ADVANCED_NOT_EXPORTED = "advanced_not_exported";
  public static final boolean KEY_ADVANCED_NOT_EXPORTED_DEFAULT = false;
  public static final String KEY_ADVANCED_ROOT_INTEGRATION = "advanced_root_integration";
  public static final boolean KEY_ADVANCED_ROOT_INTEGRATION_DEFAULT = false;

  public static void start(Context context) {
      Intent starter = new Intent(context, SettingsActivity.class);
      context.startActivity(starter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public boolean onIsMultiPane() {
    return isXLargeTablet(this);
  }

  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  protected boolean isValidFragment(String fragmentName) {
    return PreferenceFragment.class.getName().equals(fragmentName)
      || SortPreferenceFragment.class.getName().equals(fragmentName)
      || AdvancedPreferenceFragment.class.getName().equals(fragmentName);
  }

  public static class SortPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_sorting);
      setHasOptionsMenu(true);
      bindPreferenceSummaryToValue(findPreference(KEY_SORT_ORDER));
      bindPreferenceSummaryToValue(findPreference(KEY_SORT_BY));
    }
  }

  public static class AdvancedPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_advanced);
      setHasOptionsMenu(true);
      AppPreferences appPreferences = new AppPreferences(getActivity());
      SwitchPreference rootIntegration =
        (SwitchPreference) findPreference(KEY_ADVANCED_ROOT_INTEGRATION);
      rootIntegration.setOnPreferenceChangeListener((preference, newValue) -> {
        if (appPreferences.isProVersion()) {
          CheckRootTask checkRootTask = new CheckRootTask(status -> {
            if (getActivity() != null && isAdded()) {
              if (status == CheckRootTask.ROOT_IS_NOT_AVAILABLE) {
                rootIntegration.setChecked(false);
                Toast.makeText(getActivity(), R.string.settings_error_root_not_available,
                  Toast.LENGTH_SHORT).show();
              } else if (status == CheckRootTask.ACCESS_IS_NOT_GIVEN) {
                rootIntegration.setChecked(false);
                Toast.makeText(getActivity(), R.string.settings_error_root_not_granted,
                  Toast.LENGTH_SHORT).show();
              }
            }
          });
          checkRootTask.execute();
          return true;
        } else {
          new AlertDialog.Builder(getActivity())
            .setTitle(R.string.pro_version_dialog_title)
            .setMessage(R.string.pro_version_unlock_root_integration)
            .setPositiveButton(R.string.pro_version_get,
              (dialog, which) -> PurchaseActivity.start(getActivity()))
            .show();
          return false;
        }
      });
    }
  }

  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
    (preference, value) -> {
      String stringValue = value.toString();
      if (preference instanceof ListPreference) {
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);
        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
      } else {
        preference.setSummary(stringValue);
      }
      return true;
    };

  private static boolean isXLargeTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
      & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  private static void bindPreferenceSummaryToValue(Preference preference) {
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
      PreferenceManager.getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(), ""));
  }
}
