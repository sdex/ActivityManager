package com.sdex.activityrunner.preferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.sdex.activityrunner.R;

public class SortPreferenceFragment extends PreferenceFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.pref_sorting);
    setHasOptionsMenu(true);
    bindPreferenceSummaryToValue(findPreference(SettingsActivity.KEY_SORT_ORDER));
    bindPreferenceSummaryToValue(findPreference(SettingsActivity.KEY_SORT_BY));
  }

  private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
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

  private void bindPreferenceSummaryToValue(Preference preference) {
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
      PreferenceManager.getDefaultSharedPreferences(preference.getContext())
        .getString(preference.getKey(), ""));
  }
}
