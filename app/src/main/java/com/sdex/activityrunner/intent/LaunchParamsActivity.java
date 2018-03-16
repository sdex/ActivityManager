package com.sdex.activityrunner.intent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;
import java.util.ArrayList;
import java.util.List;

public class LaunchParamsActivity extends BaseActivity
  implements ValueInputDialog.OnValueInputDialogCallback,
  SingleSelectionDialog.OnItemSelectedCallback,
  MultiSelectionDialog.OnItemsSelectedCallback {

  private static final String ARG_ACTIVITY_MODEL = "arg_activity_model";
  private static final String STATE_LAUNCH_PARAMS = "state_launch_params";

  @BindView(R.id.image_package_name)
  ImageView packageNameImageView;
  @BindView(R.id.package_name)
  TextView packageNameView;
  @BindView(R.id.image_class_name)
  ImageView classNameImageView;
  @BindView(R.id.class_name)
  TextView classNameView;
  @BindView(R.id.image_data)
  ImageView dataImageView;
  @BindView(R.id.data)
  TextView dataView;
  @BindView(R.id.image_action)
  ImageView actionImageView;
  @BindView(R.id.action)
  TextView actionView;
  @BindView(R.id.image_mime_type)
  ImageView mimeTypeImageView;
  @BindView(R.id.mime_type)
  TextView mimeTypeView;
  @BindView(R.id.image_categories)
  ImageView categoriesImageView;
  @BindView(R.id.image_flags)
  ImageView flagsImageView;
  @BindView(R.id.list_categories)
  RecyclerView listCategoriesView;
  @BindView(R.id.list_flags)
  RecyclerView listFlagsView;

  private LaunchParams launchParams = new LaunchParams();

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
    ButterKnife.bind(this);

    FrameLayout adsContainer = findViewById(R.id.ads_container);
    AdsHandler adsHandler = new AdsHandler(this, adsContainer);
    adsHandler.init(this, R.string.ad_banner_unit_id);

    enableBackButton();
    final ActivityModel activityModel = (ActivityModel)
      getIntent().getSerializableExtra(ARG_ACTIVITY_MODEL);
    if (activityModel != null) {
      setTitle(activityModel.getName());
      launchParams.setPackageName(activityModel.getPackageName());
      launchParams.setClassName(activityModel.getClassName());
    }

    if (savedInstanceState != null) {
      launchParams = savedInstanceState.getParcelable(STATE_LAUNCH_PARAMS);
    }

    configureRecyclerView(listCategoriesView);
    configureRecyclerView(listFlagsView);
    categoriesAdapter = new LaunchParamsListAdapter();
    categoriesAdapter.setHasStableIds(true);
    listCategoriesView.setAdapter(categoriesAdapter);
    flagsAdapter = new LaunchParamsListAdapter();
    flagsAdapter.setHasStableIds(true);
    listFlagsView.setAdapter(flagsAdapter);

    bindInputValueDialog(R.id.container_package_name, R.string.launch_param_package_name);
    bindInputValueDialog(R.id.container_class_name, R.string.launch_param_class_name);
    bindInputValueDialog(R.id.container_data, R.string.launch_param_data);
    bindSingleSelectionDialog(R.id.container_action, R.string.launch_param_action,
      new ActionSource());
    bindSingleSelectionDialog(R.id.container_mime_type, R.string.launch_param_mime_type,
      new MimeTypeSource());
    bindMultiSelectionDialog(R.id.categories_click_interceptor, R.string.launch_param_categories,
      new CategoriesSource());
    bindMultiSelectionDialog(R.id.flags_click_interceptor, R.string.launch_param_flags,
      new FlagsSource());

    findViewById(R.id.launch).setOnClickListener(v -> {
      LaunchParamsIntentConverter converter = new LaunchParamsIntentConverter(launchParams);
      final Intent intent = converter.convert();
      IntentUtils.launchActivity(LaunchParamsActivity.this, intent);
    });

    showLaunchParams();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(STATE_LAUNCH_PARAMS, launchParams);
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

  private void bindInputValueDialog(int viewId, int type) {
    findViewById(viewId).setOnClickListener(v -> {
      String initialValue = getValueInitialPosition(type);
      ValueInputDialog dialog = ValueInputDialog.newInstance(type, initialValue);
      dialog.show(getSupportFragmentManager(), ValueInputDialog.TAG);
    });
  }

  private void bindSingleSelectionDialog(int viewId, int type, SelectionDialogSource source) {
    findViewById(viewId).setOnClickListener(v -> {
      int initialPosition = getSingleSelectionInitialPosition(type);
      SingleSelectionDialog dialog =
        SingleSelectionDialog.newInstance(type, source, initialPosition);
      dialog.show(getSupportFragmentManager(), SingleSelectionDialog.TAG);
    });
  }

  private void bindMultiSelectionDialog(int viewId, int type, SelectionDialogSource source) {
    findViewById(viewId).setOnClickListener(v -> {
      ArrayList<Integer> initialPositions = getMultiSelectionInitialPositions(type);
      MultiSelectionDialog dialog =
        MultiSelectionDialog.newInstance(type, source, initialPositions);
      dialog.show(getSupportFragmentManager(), MultiSelectionDialog.TAG);
    });
  }

  private String getValueInitialPosition(int type) {
    switch (type) {
      case R.string.launch_param_package_name:
        return launchParams.getPackageName();
      case R.string.launch_param_class_name:
        return launchParams.getClassName();
      case R.string.launch_param_data:
        return launchParams.getData();
      default:
        throw new IllegalStateException("Unknown type " + type);
    }
  }

  private int getSingleSelectionInitialPosition(int type) {
    switch (type) {
      case R.string.launch_param_action:
        return launchParams.getAction();
      case R.string.launch_param_mime_type:
        return launchParams.getMimeType();
      default:
        throw new IllegalStateException("Unknown type " + type);
    }
  }

  private ArrayList<Integer> getMultiSelectionInitialPositions(int type) {
    switch (type) {
      case R.string.launch_param_categories:
        return launchParams.getCategories();
      case R.string.launch_param_flags:
        return launchParams.getFlags();
      default:
        throw new IllegalStateException("Unknown type " + type);
    }
  }

  private void showLaunchParams() {
    final String packageName = launchParams.getPackageName();
    packageNameView.setText(packageName);
    updateIcon(packageNameImageView, packageName);
    final String className = launchParams.getClassName();
    classNameView.setText(className);
    updateIcon(classNameImageView, className);
    final String data = launchParams.getData();
    dataView.setText(data);
    updateIcon(dataImageView, data);
    final String actionValue = launchParams.getActionValue();
    actionView.setText(actionValue);
    updateIcon(actionImageView, actionValue);
    final String mimeTypeValue = launchParams.getMimeTypeValue();
    mimeTypeView.setText(mimeTypeValue);
    updateIcon(mimeTypeImageView, mimeTypeValue);
    final ArrayList<String> categoriesValues = launchParams.getCategoriesValues();
    categoriesAdapter.setItems(categoriesValues);
    updateIcon(categoriesImageView, categoriesValues);
    final ArrayList<String> flagsValues = launchParams.getFlagsValues();
    flagsAdapter.setItems(flagsValues);
    updateIcon(flagsImageView, flagsValues);
  }

  private void updateIcon(ImageView imageView, String text) {
    imageView.setImageResource(TextUtils.isEmpty(text) ?
      R.drawable.ic_assignment : R.drawable.ic_assignment_done);
  }

  private void updateIcon(ImageView imageView, List list) {
    imageView.setImageResource((list == null || list.isEmpty()) ?
      R.drawable.ic_assignment : R.drawable.ic_assignment_done);
  }
}
