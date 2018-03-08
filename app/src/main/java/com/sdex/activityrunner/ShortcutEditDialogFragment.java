package com.sdex.activityrunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.sdex.activityrunner.info.MyActivityInfo;
import com.sdex.activityrunner.util.LauncherIconCreator;

public class ShortcutEditDialogFragment extends DialogFragment {

  protected MyActivityInfo activityInfo;
  protected EditText textName;
  protected ImageButton imageIcon;

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    ComponentName activity = getArguments().getParcelable("activityInfo");
    this.activityInfo = new MyActivityInfo(activity, getActivity().getPackageManager());

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.dialog_edit_activity, null);

    textName = view.findViewById(R.id.editText_name);
    textName.setText(this.activityInfo.getName());

    imageIcon = view.findViewById(R.id.iconButton);
    imageIcon.setImageBitmap(this.activityInfo.getIcon());

    builder.setTitle(R.string.context_action_edit)
      .setView(view)
      .setPositiveButton(R.string.context_action_shortcut,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            activityInfo.setName(textName.getText().toString());
            LauncherIconCreator.createLauncherIcon(getActivity(), activityInfo);
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
