package com.sdex.activityrunner;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.util.GlideApp;
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.activityrunner.util.ObjectsCompat;

public class AddShortcutDialogFragment extends DialogFragment {

  public static final String TAG = "AddShortcutDialogFragment";

  public static final String ARG_ACTIVITY_MODEL = "arg_activity_model";

  public static AddShortcutDialogFragment newInstance(ActivityModel activityModel) {
    Bundle args = new Bundle(1);
    args.putSerializable(AddShortcutDialogFragment.ARG_ACTIVITY_MODEL, activityModel);
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

    AlertDialog.Builder builder =
      new AlertDialog.Builder(ObjectsCompat.requireNonNull(getActivity()));
    View view = View.inflate(getActivity(), R.layout.dialog_add_shortcut, null);
    final EditText textName = view.findViewById(R.id.editText_name);
    if (activityModel != null) {
      textName.setText(activityModel.getName());
      textName.setSelection(textName.getText().length());
      final ImageView imageIcon = view.findViewById(R.id.iconButton);
      GlideApp.with(this)
        .load(activityModel.getIconPath())
        .apply(new RequestOptions()
          .fitCenter())
        .into(imageIcon);
      builder.setTitle(R.string.context_action_edit)
        .setView(view)
        .setPositiveButton(R.string.context_action_shortcut,
          (dialog, which) -> {
            activityModel.setName(textName.getText().toString());
            IntentUtils.createLauncherIcon(getActivity(), activityModel);
          })
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> getDialog().cancel());
    }

    return builder.create();
  }
}
