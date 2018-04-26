package com.sdex.activityrunner.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.glide.GlideApp;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter;
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter;
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.activityrunner.util.ObjectsCompat;

public class AddShortcutDialogFragment extends DialogFragment {

  public static final String TAG = "AddShortcutDialogFragment";

  public static final String ARG_ACTIVITY_MODEL = "arg_activity_model";
  public static final String ARG_HISTORY_MODEL = "arg_history_model";

  public static AddShortcutDialogFragment newInstance(ActivityModel activityModel) {
    Bundle args = new Bundle(1);
    args.putSerializable(AddShortcutDialogFragment.ARG_ACTIVITY_MODEL, activityModel);
    AddShortcutDialogFragment fragment = new AddShortcutDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static AddShortcutDialogFragment newInstance(HistoryModel historyModel) {
    Bundle args = new Bundle(1);
    args.putSerializable(AddShortcutDialogFragment.ARG_HISTORY_MODEL, historyModel);
    AddShortcutDialogFragment fragment = new AddShortcutDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final ActivityModel activityModel =
      (ActivityModel) ObjectsCompat.requireNonNull(getArguments())
        .getSerializable(ARG_ACTIVITY_MODEL);
    final HistoryModel historyModel =
      (HistoryModel) ObjectsCompat.requireNonNull(getArguments())
        .getSerializable(ARG_HISTORY_MODEL);

    AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    View view = View.inflate(getActivity(), R.layout.dialog_add_shortcut, null);
    final TextInputLayout labelViewLayout = view.findViewById(R.id.value_layout);
    final EditText textName = view.findViewById(R.id.shortcut_name);
    if (activityModel != null) {
      textName.setText(activityModel.getName());
      textName.setSelection(textName.getText().length());
    }
    final ImageView imageIcon = view.findViewById(R.id.app_icon);
    GlideApp.with(this)
      .load(activityModel != null ? activityModel : R.mipmap.ic_launcher)
      .apply(new RequestOptions()
        .fitCenter())
      .into(imageIcon);
    builder.setTitle(R.string.context_action_edit)
      .setView(view)
      .setPositiveButton(R.string.context_action_shortcut, null)
      .setNegativeButton(android.R.string.cancel, null);
    AlertDialog alertDialog = builder.create();
    alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
      .setOnClickListener(v -> {
        labelViewLayout.setError(null);
        String shortcutName = textName.getText().toString();
        if (TextUtils.isEmpty(shortcutName)) {
          labelViewLayout.setError(getString(R.string.shortcut_name_empty));
          return;
        }
        if (activityModel != null) {
          activityModel.setName(shortcutName);
          IntentUtils.createLauncherIcon(getActivity(), activityModel);
        } else {
          createHistoryModelShortcut(historyModel, shortcutName);
        }
        dismiss();
      }));
    return alertDialog;
  }

  private void createHistoryModelShortcut(HistoryModel historyModel, String shortcutName) {
    HistoryToLaunchParamsConverter historyToLaunchParamsConverter =
      new HistoryToLaunchParamsConverter(historyModel);
    LaunchParams launchParams = historyToLaunchParamsConverter.convert();
    LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
    Intent intent = converter.convert();
    IntentUtils.createLauncherIcon(getActivity(), shortcutName, intent, R.mipmap.ic_launcher);
  }
}
