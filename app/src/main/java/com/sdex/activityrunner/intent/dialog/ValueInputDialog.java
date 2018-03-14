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

public class ValueInputDialog extends DialogFragment {

  public static final String TAG = "ValueInputDialog";

  private static final String ARG_TYPE = "arg_type";
  private static final String ARG_INITIAL_VALUE = "arg_initial_value";

  private OnValueInputDialogCallback callback;

  public static ValueInputDialog newInstance(int type, String initialValue) {
    Bundle args = new Bundle(2);
    args.putInt(ARG_TYPE, type);
    args.putString(ARG_INITIAL_VALUE, initialValue);
    ValueInputDialog fragment = new ValueInputDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final int type;
    final String initialValue;
    if (getArguments() != null) {
      type = getArguments().getInt(ARG_TYPE);
      initialValue = getArguments().getString(ARG_INITIAL_VALUE, "");
    } else {
      throw new NullPointerException();
    }
    final AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    View view = View.inflate(getActivity(), R.layout.dialog_input_value, null);
    final EditText valueView = view.findViewById(R.id.value);
    valueView.setText(initialValue);
    valueView.setSelection(initialValue.length());
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
