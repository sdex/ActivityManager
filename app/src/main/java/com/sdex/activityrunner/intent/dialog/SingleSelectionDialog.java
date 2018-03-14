package com.sdex.activityrunner.intent.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource;
import com.sdex.activityrunner.util.ObjectsCompat;
import java.util.ArrayList;

public class SingleSelectionDialog extends DialogFragment {

  public static final String TAG = "SingleSelectionDialog";

  private static final String ARG_TYPE = "arg_type";
  private static final String ARG_SOURCE = "arg_source";
  private static final String ARG_INITIAL_POSITION = "arg_initial_position";

  private OnItemSelectedCallback callback;

  public static SingleSelectionDialog newInstance(int type, SelectionDialogSource source,
    int initialPosition) {
    Bundle args = new Bundle(3);
    args.putInt(ARG_TYPE, type);
    args.putParcelable(ARG_SOURCE, source);
    args.putInt(ARG_INITIAL_POSITION, initialPosition);
    SingleSelectionDialog fragment = new SingleSelectionDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final int type;
    final SelectionDialogSource source;
    final int initialPosition;
    if (getArguments() != null) {
      type = getArguments().getInt(ARG_TYPE);
      source = getArguments().getParcelable(ARG_SOURCE);
      initialPosition = getArguments().getInt(ARG_INITIAL_POSITION);
    } else {
      throw new NullPointerException();
    }
    final ArrayList<String> list = ObjectsCompat.requireNonNull(source).getList();
    final AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    builder.setSingleChoiceItems(list.toArray(new String[list.size()]),
      initialPosition, (dialog, which) -> {
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
        + " must implement OnItemSelectedCallback");
    }
  }

  public interface OnItemSelectedCallback {

    void onItemSelected(int type, int position);
  }
}