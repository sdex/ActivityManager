package com.sdex.activityrunner.intent.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.intent.LaunchParamsExtra;
import com.sdex.activityrunner.intent.LaunchParamsExtraType;
import com.sdex.activityrunner.util.ObjectsCompat;

public class KeyValueInputDialog extends DialogFragment {

  public static final String TAG = "KeyValueInputDialog";

  private static final String ARG_INITIAL_EXTRA = "arg_initial_extra";
  private static final String ARG_POSITION = "arg_position";

  private OnKeyValueInputDialogCallback callback;

  public static KeyValueInputDialog newInstance(LaunchParamsExtra initialExtra, int position) {
    Bundle args = new Bundle(2);
    args.putParcelable(ARG_INITIAL_EXTRA, initialExtra);
    args.putInt(ARG_POSITION, position);
    KeyValueInputDialog fragment = new KeyValueInputDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final LaunchParamsExtra initialExtra;
    final int position;
    if (getArguments() != null) {
      initialExtra = getArguments().getParcelable(ARG_INITIAL_EXTRA);
      position = getArguments().getInt(ARG_POSITION);
    } else {
      throw new NullPointerException();
    }
    final AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    View view = View.inflate(getActivity(), R.layout.dialog_input_key_value, null);
    final EditText keyView = view.findViewById(R.id.key);
    final EditText valueView = view.findViewById(R.id.value);

    final TextInputLayout keyLayout = view.findViewById(R.id.key_layout);
    final TextInputLayout valueLayout = view.findViewById(R.id.value_layout);

    if (initialExtra != null) {
      keyLayout.setHintAnimationEnabled(false);
      valueLayout.setHintAnimationEnabled(false);
      keyView.setText(initialExtra.getKey());
      valueView.setText(initialExtra.getValue());
      keyView.setSelection(keyView.getText().length());
      keyLayout.setHintAnimationEnabled(true);
      valueLayout.setHintAnimationEnabled(true);
      setSelectedType(view, initialExtra.getType());
    } else {
      RadioButton stringRadioBtn = view.findViewById(R.id.rb_string);
      stringRadioBtn.setChecked(true);
    }
    builder.setTitle("Add extra")
      .setView(view)
      .setPositiveButton(android.R.string.ok, null)
      .setNegativeButton(android.R.string.cancel, null);
    final AlertDialog alertDialog = builder.create();
    alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
      .setOnClickListener(v -> {
        final String newKey = keyView.getText().toString();
        final String newValue = valueView.getText().toString();
        final int type = getSelectedType(view);

        keyLayout.setError(null);
        valueLayout.setError(null);

        if (TextUtils.isEmpty(newKey)) {
          keyLayout.setError("Key cannot be empty");
          keyView.requestFocus();
          return;
        }

        if (TextUtils.isEmpty(newValue)) {
          valueLayout.setError("Value cannot be empty");
          valueView.requestFocus();
          return;
        }

        if (!isExtraFormatValid(type, newValue)) {
          valueLayout.setError("Incorrect value type");
          return;
        }

        LaunchParamsExtra extra = new LaunchParamsExtra();
        extra.setKey(newKey);
        extra.setValue(newValue);
        extra.setType(type);
        callback.onValueSet(extra, position);
        dismiss();
      }));
    return alertDialog;
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

  @LaunchParamsExtraType
  private int getSelectedType(View view) {
    RadioButton stringRadioBtn = view.findViewById(R.id.rb_string);
    if (stringRadioBtn.isChecked()) {
      return LaunchParamsExtraType.STRING;
    }
    RadioButton intRadioBtn = view.findViewById(R.id.rb_int);
    if (intRadioBtn.isChecked()) {
      return LaunchParamsExtraType.INT;
    }
    RadioButton longRadioBtn = view.findViewById(R.id.rb_long);
    if (longRadioBtn.isChecked()) {
      return LaunchParamsExtraType.LONG;
    }
    RadioButton floatRadioBtn = view.findViewById(R.id.rb_float);
    if (floatRadioBtn.isChecked()) {
      return LaunchParamsExtraType.FLOAT;
    }
    RadioButton doubleRadioBtn = view.findViewById(R.id.rb_double);
    if (doubleRadioBtn.isChecked()) {
      return LaunchParamsExtraType.DOUBLE;
    }
    RadioButton booleanRadioBtn = view.findViewById(R.id.rb_boolean);
    if (booleanRadioBtn.isChecked()) {
      return LaunchParamsExtraType.BOOLEAN;
    }
    return -1;
  }

  private void setSelectedType(View view, @LaunchParamsExtraType int type) {
    RadioButton radioButton = null;
    switch (type) {
      case LaunchParamsExtraType.STRING:
        radioButton = view.findViewById(R.id.rb_string);
        break;
      case LaunchParamsExtraType.INT:
        radioButton = view.findViewById(R.id.rb_int);
        break;
      case LaunchParamsExtraType.LONG:
        radioButton = view.findViewById(R.id.rb_long);
        break;
      case LaunchParamsExtraType.FLOAT:
        radioButton = view.findViewById(R.id.rb_float);
        break;
      case LaunchParamsExtraType.DOUBLE:
        radioButton = view.findViewById(R.id.rb_double);
        break;
      case LaunchParamsExtraType.BOOLEAN:
        radioButton = view.findViewById(R.id.rb_boolean);
        break;
    }
    if (radioButton != null) {
      radioButton.setChecked(true);
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private boolean isExtraFormatValid(int type, String value) {
    try {
      switch (type) {
        case LaunchParamsExtraType.INT:
          Integer.parseInt(value);
          break;
        case LaunchParamsExtraType.LONG:
          Long.parseLong(value);
          break;
        case LaunchParamsExtraType.FLOAT:
          Float.parseFloat(value);
          break;
        case LaunchParamsExtraType.DOUBLE:
          Double.parseDouble(value);
          break;
      }
    } catch (NumberFormatException e) {
      Log.d(TAG, "Failed to parse number");
      return false;
    }
    return true;
  }

  public interface OnKeyValueInputDialogCallback {

    void onValueSet(LaunchParamsExtra extra, int position);
  }
}
