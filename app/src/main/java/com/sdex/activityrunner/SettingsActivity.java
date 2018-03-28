package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

  public static final String KEY_SORT_ORDER = "sort_order";
  public static final String KEY_SORT_BY = "sort_by";
  public static final String KEY_SORT_BY_DEFAULT = "0";

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
      || SortPreferenceFragment.class.getName().equals(fragmentName);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
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
