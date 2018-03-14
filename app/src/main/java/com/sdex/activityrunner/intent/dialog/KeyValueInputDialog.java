package com.sdex.activityrunner.intent.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.util.ObjectsCompat;

public class KeyValueInputDialog extends DialogFragment {

  public static final String TAG = "KeyValueInputDialog";

  private static final String ARG_INITIAL_KEY = "arg_initial_key";
  private static final String ARG_INITIAL_VALUE = "arg_initial_value";

  private OnKeyValueInputDialogCallback callback;

  public static ValueInputDialog newInstance(String initialKey, String initialValue) {
    Bundle args = new Bundle(2);
    args.putString(ARG_INITIAL_KEY, initialKey);
    args.putString(ARG_INITIAL_VALUE, initialValue);
    ValueInputDialog fragment = new ValueInputDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final String initialKey;
    final String initialValue;
    if (getArguments() != null) {
      initialKey = getArguments().getString(ARG_INITIAL_KEY, "");
      initialValue = getArguments().getString(ARG_INITIAL_VALUE, "");
    } else {
      throw new NullPointerException();
    }
    final AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    View view = View.inflate(getActivity(), R.layout.dialog_input_key_value, null);
    final EditText keyView = view.findViewById(R.id.key);
    final EditText valueView = view.findViewById(R.id.value);
    keyView.setText(initialKey);
    valueView.setText(initialValue);
    keyView.setSelection(initialKey.length());
    builder.setTitle("Add extra")
      .setView(view)
      .setPositiveButton(android.R.string.ok, (dialog, which) -> {
        final String newKey = keyView.getText().toString();
        final String newValue = valueView.getText().toString();
        callback.onValueSet(newKey, newValue);
      })
      .setNegativeButton(android.R.string.cancel, null);
    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      callback = (OnKeyValueInputDialogCallback) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnKeyValueInputDialogCallback");
    }
  }

  public interface OnKeyValueInputDialogCallback {

    void onValueSet(String key, String value);
  }
}
