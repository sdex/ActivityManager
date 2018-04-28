package com.sdex.activityrunner.intent;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdex.activityrunner.GetPremiumDialog;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.intent.LaunchParamsExtraListAdapter.Callback;
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter;
import com.sdex.activityrunner.intent.dialog.ExtraInputDialog;
import com.sdex.activityrunner.intent.dialog.MultiSelectionDialog;
import com.sdex.activityrunner.intent.dialog.SingleSelectionDialog;
import com.sdex.activityrunner.intent.dialog.ValueInputDialog;
import com.sdex.activityrunner.intent.dialog.source.ActionSource;
import com.sdex.activityrunner.intent.dialog.source.CategoriesSource;
import com.sdex.activityrunner.intent.dialog.source.FlagsSource;
import com.sdex.activityrunner.intent.dialog.source.MimeTypeSource;
import com.sdex.activityrunner.intent.dialog.source.SelectionDialogSource;
import com.sdex.activityrunner.intent.history.HistoryActivity;
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsDelegate;
import com.sdex.commons.ads.AppPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntentBuilderActivity extends BaseActivity
  implements ValueInputDialog.OnValueInputDialogCallback,
  SingleSelectionDialog.OnItemSelectedCallback,
  MultiSelectionDialog.OnItemsSelectedCallback,
  ExtraInputDialog.OnKeyValueInputDialogCallback {

  private static final String ARG_ACTIVITY_MODEL = "arg_activity_model";
  private static final String STATE_LAUNCH_PARAMS = "state_launch_params";

  private static final int EXTRAS_LIMIT = 3;

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
  @BindView(R.id.image_extras)
  ImageView extrasImageView;
  @BindView(R.id.image_categories)
  ImageView categoriesImageView;
  @BindView(R.id.image_flags)
  ImageView flagsImageView;
  @BindView(R.id.add_extra)
  TextView addExtraView;
  @BindView(R.id.list_extras)
  RecyclerView listExtrasView;
  @BindView(R.id.list_categories)
  RecyclerView listCategoriesView;
  @BindView(R.id.list_flags)
  RecyclerView listFlagsView;

  private LaunchParams launchParams = new LaunchParams();

  private LaunchParamsListAdapter categoriesAdapter;
  private LaunchParamsListAdapter flagsAdapter;
  private LaunchParamsExtraListAdapter extraAdapter;
  private LaunchParamsViewModel viewModel;

  private AppPreferences appPreferences;
  private AdsDelegate adsDelegate;

  public static void start(Context context, ActivityModel activityModel) {
    Intent starter = new Intent(context, IntentBuilderActivity.class);
    starter.putExtra(ARG_ACTIVITY_MODEL, activityModel);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_intent_builder;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    viewModel = ViewModelProviders.of(this).get(LaunchParamsViewModel.class);

    appPreferences = new AppPreferences(this);

    FrameLayout adsContainer = findViewById(R.id.ads_container);
    adsDelegate = new AdsDelegate(appPreferences, adsContainer);
    adsDelegate.initBanner(this, R.string.ad_banner_unit_id);

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

    configureRecyclerView(listExtrasView);
    configureRecyclerView(listCategoriesView);
    configureRecyclerView(listFlagsView);

    extraAdapter = new LaunchParamsExtraListAdapter(new Callback() {
      @Override
      public void onItemSelected(int position) {
        final LaunchParamsExtra extra = launchParams.getExtras().get(position);
        DialogFragment dialog = ExtraInputDialog.newInstance(extra, position);
        dialog.show(getSupportFragmentManager(), ExtraInputDialog.TAG);
      }

      @Override
      public void removeItem(int position) {
        launchParams.getExtras().remove(position);
        extraAdapter.notifyDataSetChanged();
        listExtrasView.requestLayout();
        updateExtrasAdd();
      }
    });
    extraAdapter.setHasStableIds(true);
    listExtrasView.setAdapter(extraAdapter);
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
    bindKeyValueDialog(R.id.container_extras);
    bindMultiSelectionDialog(R.id.categories_click_interceptor, R.string.launch_param_categories,
      new CategoriesSource());
    bindMultiSelectionDialog(R.id.flags_click_interceptor, R.string.launch_param_flags,
      new FlagsSource());

    findViewById(R.id.launch).setOnClickListener(v -> {
      viewModel.addToHistory(launchParams);
      LaunchParamsToIntentConverter converter = new LaunchParamsToIntentConverter(launchParams);
      final Intent intent = converter.convert();
      IntentUtils.launchActivity(IntentBuilderActivity.this, intent);
    });

    showLaunchParams();
  }

  @Override
  protected void onResume() {
    super.onResume();
    adsDelegate.detachBottomBannerIfNeed();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(STATE_LAUNCH_PARAMS, launchParams);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == HistoryActivity.REQUEST_CODE && resultCode == RESULT_OK) {
      launchParams = data.getParcelableExtra(HistoryActivity.RESULT);
      showLaunchParams();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.launch_param, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_history: {
        final Intent intent = HistoryActivity.getLaunchIntent(this);
        startActivityForResult(intent, HistoryActivity.REQUEST_CODE);
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
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

  @Override
  public void onValueSet(LaunchParamsExtra extra, int position) {
    final ArrayList<LaunchParamsExtra> extras = launchParams.getExtras();
    if (position == -1) {
      extras.add(extra);
    } else {
      extras.set(position, extra);
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

  private void bindKeyValueDialog(int viewId) {
    findViewById(viewId).setOnClickListener(v -> {
      final int size = launchParams.getExtras().size();
      if (size >= EXTRAS_LIMIT && !appPreferences.isProVersion()) {
        GetPremiumDialog dialog =
          GetPremiumDialog.newInstance(R.string.pro_version_unlock_extras);
        dialog.show(getSupportFragmentManager(), GetPremiumDialog.TAG);
        return;
      }
      DialogFragment dialog = ExtraInputDialog.newInstance(null, -1);
      dialog.show(getSupportFragmentManager(), ExtraInputDialog.TAG);
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
    final ArrayList<LaunchParamsExtra> extras = launchParams.getExtras();
    extraAdapter.setItems(extras);
    updateIcon(extrasImageView, extras);
    updateExtrasAdd();
    final ArrayList<String> categoriesValues = launchParams.getCategoriesValues();
    categoriesAdapter.setItems(categoriesValues);
    updateIcon(categoriesImageView, categoriesValues);
    final ArrayList<String> flagsValues = launchParams.getFlagsValues();
    flagsAdapter.setItems(flagsValues);
    updateIcon(flagsImageView, flagsValues);
  }

  private void updateExtrasAdd() {
    final ArrayList<LaunchParamsExtra> extras = launchParams.getExtras();
    if (extras.isEmpty()) {
      addExtraView.setVisibility(View.GONE);
    } else {
      addExtraView.setVisibility(View.VISIBLE);
    }
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
