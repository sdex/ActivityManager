package com.sdex.activityrunner.intent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import com.sdex.activityrunner.R;

public class ValueInputDialog extends DialogFragment {

  public static final String TAG = "ValueInputDialog";

  private static final String ARG_TYPE = "arg_type";
  private static final String ARG_OLD_VALUE = "arg_old_value";

  private OnValueInputDialogCallback callback;

  public static ValueInputDialog newInstance(int type, String oldValue) {
    Bundle args = new Bundle(2);
    args.putInt(ARG_TYPE, type);
    args.putString(ARG_OLD_VALUE, oldValue);
    ValueInputDialog fragment = new ValueInputDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final int type;
    final String oldValue;
    if (getArguments() != null) {
      type = getArguments().getInt(ARG_TYPE);
      oldValue = getArguments().getString(ARG_OLD_VALUE, "");
    } else {
      throw new NullPointerException();
    }
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    View view = View.inflate(getActivity(), R.layout.dialog_input_value, null);
    final EditText valueView = view.findViewById(R.id.value);
    valueView.setText(oldValue);
    valueView.setSelection(oldValue.length());
    builder.setTitle(type)
      .setView(view)
      .setPositiveButton(android.R.string.ok, (dialog, which) -> {
        final String newValue = valueView.getText().toString();
        callback.onValueSet(type, newValue);
      })
      .setNegativeButton(android.R.string.cancel, null);
    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      callback = (OnValueInputDialogCallback) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnValueInputDialogCallback");
    }
  }

  public interface OnValueInputDialogCallback {

    void onValueSet(int type, String value);
  }
}
