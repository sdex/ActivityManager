package com.sdex.activityrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.request.RequestOptions;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.util.GlideApp;
import com.sdex.activityrunner.util.IntentUtils;

public class ShortcutEditDialogFragment extends DialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final ActivityModel activityModel = (ActivityModel)
      getArguments().getSerializable("activityInfo");

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.dialog_add_shortcut, null);

    final EditText textName = view.findViewById(R.id.editText_name);
    textName.setText(activityModel.getName());

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
    return builder.create();
  }
}
