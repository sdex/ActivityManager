package com.sdex.activityrunner.intent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import java.util.ArrayList;

public class SingleSelectionDialog extends DialogFragment {

  public static final String TAG = "SingleSelectionDialog";

  private static final String ARG_TYPE = "arg_type";
  private static final String ARG_ITEMS = "arg_items";

  private OnItemSelectedCallback callback;

  public static SingleSelectionDialog newInstance(int type, ArrayList<String> items) {
    Bundle args = new Bundle(2);
    args.putInt(ARG_TYPE, type);
    args.putStringArrayList(ARG_ITEMS, items);
    SingleSelectionDialog fragment = new SingleSelectionDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final int type;
    final ArrayList<String> items;
    if (getArguments() != null) {
      type = getArguments().getInt(ARG_TYPE);
      items = getArguments().getStringArrayList(ARG_ITEMS);
    } else {
      throw new NullPointerException();
    }
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setSingleChoiceItems(items.toArray(new String[items.size()]),
      0, (dialog, which) -> {
        callback.onItemSelected(type, which);
        dismiss();
      });
    builder.setTitle(type);
    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      callback = (OnItemSelectedCallback) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnValueInputDialogCallback");
    }
  }

  public interface OnItemSelectedCallback {

    void onItemSelected(int type, int position);
  }
}