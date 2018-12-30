package com.sdex.commons;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
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
