package com.sdex.activityrunner.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.sdex.activityrunner.R;
import com.sdex.activityrunner.preferences.SettingsActivity;

public class EnableNotExportedActivitiesDialog extends DialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
      .setTitle("Non-exported activities")
      .setMessage("You can enable displaying non-exported activities in settings")
      .setPositiveButton(R.string.action_settings, (dialog, which) ->
        SettingsActivity.start(getActivity(), SettingsActivity.ADVANCED))
      .setNegativeButton(android.R.string.cancel, null)
      .create();
  }
}
