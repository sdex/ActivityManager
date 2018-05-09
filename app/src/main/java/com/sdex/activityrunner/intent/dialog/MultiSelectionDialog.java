package com.sdex.activityrunner.intent.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;

import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource;

import java.util.ArrayList;

public class MultiSelectionDialog extends DialogFragment {

  public static final String TAG = "MultiSelectionDialog";

  private static final String ARG_TYPE = "arg_type";
  private static final String ARG_SOURCE = "arg_source";
  private static final String ARG_INITIAL_POSITIONS = "arg_initial_positions";

  private OnItemsSelectedCallback callback;
  private SparseBooleanArray selectedItems = new SparseBooleanArray();

  public static MultiSelectionDialog newInstance(int type,
    SelectionDialogSource source, ArrayList<Integer> initialPositions) {
    Bundle args = new Bundle(3);
    args.putInt(ARG_TYPE, type);
    args.putParcelable(ARG_SOURCE, source);
    args.putIntegerArrayList(ARG_INITIAL_POSITIONS, initialPositions);
    MultiSelectionDialog fragment = new MultiSelectionDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final int type;
    final SelectionDialogSource source;
    final ArrayList<Integer> initialPositions;
    if (getArguments() != null) {
      type = getArguments().getInt(ARG_TYPE);
      source = getArguments().getParcelable(ARG_SOURCE);
      initialPositions = (getArguments().getIntegerArrayList(ARG_INITIAL_POSITIONS));
    } else {
      throw new NullPointerException();
    }
    final ArrayList<String> list = source.getList();
    boolean[] checkedItems = new boolean[list.size()];
    for (int i = 0; i < checkedItems.length; i++) {
      final boolean checked = initialPositions.contains(i);
      checkedItems[i] = checked;
      selectedItems.put(i, checked);
    }
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMultiChoiceItems(list.toArray(new String[list.size()]), checkedItems,
      (dialog, which, isChecked) -> selectedItems.put(which, isChecked));
    builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
      ArrayList<Integer> selectedPositions = new ArrayList<>();
      for (int i = 0; i < selectedItems.size(); i++) {
        final int key = selectedItems.keyAt(i);
        final boolean isSelected = selectedItems.get(key, false);
        if (isSelected) {
          selectedPositions.add(key);
        }
      }
      callback.onItemsSelected(type, selectedPositions);
    });
    builder.setTitle(type);
    return builder.create();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      callback = (OnItemsSelectedCallback) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
        + " must implement OnItemsSelectedCallback");
    }
  }

  public interface OnItemsSelectedCallback {

    void onItemsSelected(int type, ArrayList<Integer> positions);
  }
}
