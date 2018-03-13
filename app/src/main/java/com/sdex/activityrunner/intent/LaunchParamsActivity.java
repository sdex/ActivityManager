package com.sdex.activityrunner.intent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.commons.BaseActivity;

public class LaunchParamsActivity extends BaseActivity
  implements OnValueInputDialogCallback {

  private static final String ARG_ACTIVITY_MODEL = "arg_activity_model";

  private TextView packageNameView;
  private TextView classNameView;
  private TextView dataView;

  public static void start(Context context, ActivityModel activityModel) {
    Intent starter = new Intent(context, LaunchParamsActivity.class);
    starter.putExtra(ARG_ACTIVITY_MODEL, activityModel);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_launch_params;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    enableBackButton();
    ActivityModel activityModel = (ActivityModel)
      getIntent().getSerializableExtra(ARG_ACTIVITY_MODEL);
    setTitle(activityModel.getName());

    packageNameView = findViewById(R.id.package_name);
    classNameView = findViewById(R.id.class_name);
    dataView = findViewById(R.id.data);

    packageNameView.setText(activityModel.getPackageName());
    classNameView.setText(activityModel.getClassName());

    findViewById(R.id.container_package_name).setOnClickListener(v -> {
      ValueInputDialog dialog = ValueInputDialog.newInstance(
        R.string.launch_param_package_name,
        packageNameView.getText().toString());
      dialog.show(getSupportFragmentManager(), ValueInputDialog.TAG);
    });
    findViewById(R.id.container_class_name).setOnClickListener(v -> {
      ValueInputDialog dialog = ValueInputDialog.newInstance(
        R.string.launch_param_class_name,
        classNameView.getText().toString());
      dialog.show(getSupportFragmentManager(), ValueInputDialog.TAG);
    });
    findViewById(R.id.container_data).setOnClickListener(v -> {
      ValueInputDialog dialog = ValueInputDialog.newInstance(
        R.string.launch_param_data,
        dataView.getText().toString());
      dialog.show(getSupportFragmentManager(), ValueInputDialog.TAG);
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public void onValueSet(int type, String value) {
    switch (type) {
      case R.string.launch_param_package_name:
        packageNameView.setText(value);
        break;
      case R.string.launch_param_class_name:
        classNameView.setText(value);
        break;
      case R.string.launch_param_data:
        dataView.setText(value);
        break;
    }
  }
}
