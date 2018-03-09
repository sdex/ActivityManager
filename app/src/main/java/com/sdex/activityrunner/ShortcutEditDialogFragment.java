package com.sdex.activityrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.util.LauncherIconCreator;

public class ShortcutEditDialogFragment extends DialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    ComponentName componentName = getArguments().getParcelable("activityInfo");
    String name;
    PackageManager pm = getActivity().getPackageManager();
    ActivityInfo act;
    try {
      act = pm.getActivityInfo(componentName, 0);
      name = act.loadLabel(pm).toString();
    } catch (Exception e) {
      name = componentName.getShortClassName();
    }

    final ActivityModel activityModel = new ActivityModel(name, componentName);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.dialog_edit_activity, null);

    final EditText textName = view.findViewById(R.id.editText_name);
    textName.setText(activityModel.getName());

    final ImageView imageIcon = view.findViewById(R.id.iconButton);
//    imageIcon.setImageBitmap(activityModel.getIcon()); // TODO icon

    builder.setTitle(R.string.context_action_edit)
      .setView(view)
      .setPositiveButton(R.string.context_action_shortcut,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            activityModel.setName(textName.getText().toString());
            LauncherIconCreator.createLauncherIcon(getActivity(), activityModel);
          }
        })
      .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          getDialog().cancel();
        }
      });
    return builder.create();
  }
}
