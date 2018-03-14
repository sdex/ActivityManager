package com.sdex.activityrunner.intent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TextView;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog;
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog;
import com.sdex.activityrunner.intent.dialog.ValueInputDialog;
import com.sdex.activityrunner.intent.dialog.source.ActionSource;
import com.sdex.activityrunner.intent.dialog.source.CategoriesSource;
import com.sdex.activityrunner.intent.dialog.source.FlagsSource;
import com.sdex.activityrunner.intent.dialog.source.MimeTypeSource;
import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource;
import com.sdex.commons.BaseActivity;
import java.util.ArrayList;

public class LaunchParamsActivity extends BaseActivity
  implements ValueInputDialog.OnValueInputDialogCallback,
  SingleSelectionDialog.OnItemSelectedCallback,
  MultiSelectionDialog.OnItemsSelectedCallback {

  private static final String ARG_ACTIVITY_MODEL = "arg_activity_model";

  private final LaunchParams launchParams = new LaunchParams();

  private TextView packageNameView;
  private TextView classNameView;
  private TextView dataView;
  private TextView actionView;
  private TextView mimeTypeView;
  private LaunchParamsListAdapter categoriesAdapter;
  private LaunchParamsListAdapter flagsAdapter;

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

    launchParams.setPackageName(activityModel.getPackageName());
    launchParams.setClassName(activityModel.getClassName());

    packageNameView = findViewById(R.id.package_name);
    classNameView = findViewById(R.id.class_name);
    dataView = findViewById(R.id.data);
    actionView = findViewById(R.id.action);
    mimeTypeView = findViewById(R.id.mime_type);

    RecyclerView listCategoriesView = findViewById(R.id.list_categories);
    configureRecyclerView(listCategoriesView);
    RecyclerView listFlagsView = findViewById(R.id.list_flags);
    configureRecyclerView(listFlagsView);
    categoriesAdapter = new LaunchParamsListAdapter();
    categoriesAdapter.setHasStableIds(true);
    listCategoriesView.setAdapter(categoriesAdapter);
    flagsAdapter = new LaunchParamsListAdapter();
    flagsAdapter.setHasStableIds(true);
    listFlagsView.setAdapter(flagsAdapter);

    bindInputValueDialog(R.id.container_package_name,
      R.string.launch_param_package_name, launchParams.getPackageName());
    bindInputValueDialog(R.id.container_class_name,
      R.string.launch_param_class_name, launchParams.getClassName());
    bindInputValueDialog(R.id.container_data,
      R.string.launch_param_data, launchParams.getData());
    bindSingleSelectionDialog(R.id.container_action, R.string.launch_param_action,
      new ActionSource(), launchParams.getAction());
    bindSingleSelectionDialog(R.id.container_mime_type, R.string.launch_param_mime_type,
      new MimeTypeSource(), launchParams.getMimeType());
    bindMultiSelectionDialog(R.id.container_categories, R.string.launch_param_categories,
      new CategoriesSource(), launchParams.getCategories());
    bindMultiSelectionDialog(R.id.container_flags, R.string.launch_param_flags,
      new FlagsSource(), launchParams.getFlags());

    showLaunchParams();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public void onValueSet(int type, String value) {
    switch (type) {
      case R.string.launch_param_package_name:
        launchParams.setPackageName(value);
        break;
      case R.string.launch_param_class_name:
        launchParams.setClassName(value);
        break;
      case R.string.launch_param_data:
        launchParams.setData(value);
        break;
    }
    showLaunchParams();
  }

  @Override
  public void onItemSelected(int type, int position) {
    switch (type) {
      case R.string.launch_param_action:
        launchParams.setAction(position);
        break;
      case R.string.launch_param_mime_type:
        launchParams.setMimeType(position);
        break;
    }
    showLaunchParams();
  }

  @Override
  public void onItemsSelected(int type, ArrayList<Integer> positions) {
    switch (type) {
      case R.string.launch_param_categories:
        launchParams.setCategories(positions);
        break;
      case R.string.launch_param_flags:
        launchParams.setFlags(positions);
        break;
    }
    showLaunchParams();
  }

  private void configureRecyclerView(RecyclerView recyclerView) {
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setHasFixedSize(true);
  }

  private void bindInputValueDialog(int viewId, int type, String initialValue) {
    findViewById(viewId).setOnClickListener(v -> {
      ValueInputDialog dialog = ValueInputDialog.newInstance(type, initialValue);
      dialog.show(getSupportFragmentManager(), ValueInputDialog.TAG);
    });
  }

  private void bindSingleSelectionDialog(int viewId, int type,
    SelectionDialogSource source, int initialPosition) {
    findViewById(viewId).setOnClickListener(v -> {
      SingleSelectionDialog dialog =
        SingleSelectionDialog.newInstance(type, source, initialPosition);
      dialog.show(getSupportFragmentManager(), SingleSelectionDialog.TAG);
    });
  }

  private void bindMultiSelectionDialog(int viewId, int type,
    SelectionDialogSource source, ArrayList<Integer> initialPositions) {
    findViewById(viewId).setOnClickListener(v -> {
      MultiSelectionDialog dialog =
        MultiSelectionDialog.newInstance(type, source, initialPositions);
      dialog.show(getSupportFragmentManager(), MultiSelectionDialog.TAG);
    });
  }

  private void showLaunchParams() {
    packageNameView.setText(launchParams.getPackageName());
    classNameView.setText(launchParams.getClassName());
    dataView.setText(launchParams.getData());
    actionView.setText(launchParams.getActionValue());
    mimeTypeView.setText(launchParams.getMimeTypeValue());
    categoriesAdapter.setItems(launchParams.getCategoriesValues());
    flagsAdapter.setItems(launchParams.getFlagsValues());
  }
}
