package com.sdex.activityrunner.app;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sdex.activityrunner.AddShortcutDialogFragment;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.SettingsActivity;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.intent.LaunchParamsActivity;
import com.sdex.activityrunner.preferences.AdvancedPreferences;
import com.sdex.activityrunner.util.IntentUtils;
import com.sdex.activityrunner.util.RunActivityTask;
import com.sdex.commons.BaseActivity;

public class ActivitiesListActivity extends BaseActivity
  implements ActivitiesListAdapter.Callback {

  public static final String ARG_APPLICATION = "arg_application";

  private AdvancedPreferences advancedPreferences;

  public static void start(Context context, ApplicationModel item) {
    Intent starter = new Intent(context, ActivitiesListActivity.class);
    starter.putExtra(ARG_APPLICATION, item);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_activities_list;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ApplicationModel item = (ApplicationModel) getIntent().getSerializableExtra(ARG_APPLICATION);
    setTitle(item.getName());
    enableBackButton();
    RecyclerView list = findViewById(R.id.list);
    final Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.list_divider);
    if (dividerDrawable != null) {
      DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDrawable(dividerDrawable);
      list.addItemDecoration(dividerItemDecoration);
    }
    ActivitiesListAdapter adapter = new ActivitiesListAdapter(this, this);
    list.setAdapter(adapter);
    ActivitiesListViewModel viewModel =
      ViewModelProviders.of(this).get(ActivitiesListViewModel.class);
    viewModel.getItems(item.getPackageName()).observe(this, activityModels -> {
      adapter.submitList(activityModels);
      if (activityModels != null) {
        int size = activityModels.size();
        setSubtitle(getResources().getQuantityString(R.plurals.activities_count, size, size));
      }
    });

    SharedPreferences sharedPreferences =
      PreferenceManager.getDefaultSharedPreferences(this);
    this.advancedPreferences = new AdvancedPreferences(sharedPreferences);
  }

  @Override
  public void showShortcutDialog(ActivityModel item) {
    DialogFragment dialog = AddShortcutDialogFragment.newInstance(item);
    dialog.show(getSupportFragmentManager(), AddShortcutDialogFragment.TAG);
  }

  @Override
  public void launchActivity(ActivityModel item) {
    if (item.isExported()) {
      IntentUtils.launchActivity(this, item.getComponentName(), item.getName());
    } else {
      tryRunWithRoot(item);
    }
  }

  @Override
  public void launchActivityWithParams(ActivityModel item) {
    LaunchParamsActivity.start(this, item);
  }

  private void tryRunWithRoot(ActivityModel item) {
    if (advancedPreferences.isRootIntegrationEnabled()) {
      RunActivityTask runActivityTask =
        new RunActivityTask(item.getComponentName());
      runActivityTask.execute();
    } else {
      View view = findViewById(R.id.container);
      Snackbar.make(view, R.string.settings_error_root_not_active, Snackbar.LENGTH_SHORT)
        .setAction(R.string.action_settings,
          v -> SettingsActivity.start(ActivitiesListActivity.this, SettingsActivity.ADVANCED))
        .setActionTextColor(ContextCompat.getColor(this, R.color.yellow))
        .show();
    }
  }
}
