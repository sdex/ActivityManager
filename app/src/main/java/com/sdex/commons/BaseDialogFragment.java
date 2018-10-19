package com.sdex.commons;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class BaseDialogFragment extends DialogFragment {

  private static final String TAG = "BaseDialogFragment";

  @Override
  public void show(FragmentManager manager, String tag) {
    try {
      super.show(manager, tag);
    } catch (IllegalStateException e) {
      Log.d(TAG, "Failed to add fragment", e);
    }
  }
}
