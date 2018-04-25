package com.sdex.activityrunner;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class GetPremiumDialog extends DialogFragment {

  public static final String TAG = "GetPremiumDialog";

  private static final String ARG_MESSAGE = "arg_message";

  public static GetPremiumDialog newInstance(@StringRes int messageRes) {
    Bundle args = new Bundle();
    args.putInt(ARG_MESSAGE, messageRes);
    GetPremiumDialog fragment = new GetPremiumDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    int message = getArguments().getInt(ARG_MESSAGE);
    return new AlertDialog.Builder(getActivity())
      .setTitle(R.string.pro_version_dialog_title)
      .setMessage(message)
      .setPositiveButton(R.string.pro_version_get,
        (dialog, which) -> PurchaseActivity.start(getActivity()))
      .create();
  }
}
