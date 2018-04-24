package com.sdex.activityrunner.intent.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.sdex.activityrunner.AddShortcutDialogFragment;
import com.sdex.activityrunner.PurchaseActivity;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.LaunchParams;
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AppPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends BaseActivity {

  public static final String RESULT = "result";

  public static final int REQUEST_CODE = 111;

  @BindView(R.id.list)
  RecyclerView recyclerView;

  private AppPreferences appPreferences;
  private HistoryListAdapter adapter;
  private HistoryViewModel viewModel;

  public static Intent getLaunchIntent(Context context) {
    return new Intent(context, HistoryActivity.class);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_history;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
    appPreferences = new AppPreferences(this);

    enableBackButton();

    adapter = new HistoryListAdapter((item, position) -> {
      HistoryToLaunchParamsConverter historyToLaunchParamsConverter =
        new HistoryToLaunchParamsConverter(item);
      LaunchParams launchParams = historyToLaunchParamsConverter.convert();
      Intent data = new Intent();
      data.putExtra(RESULT, launchParams);
      setResult(RESULT_OK, data);
      finish();
    });
    adapter.setHasStableIds(true);
    final Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.list_divider);
    if (dividerDrawable != null) {
      DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDrawable(dividerDrawable);
      recyclerView.addItemDecoration(dividerItemDecoration);
    }
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
    registerForContextMenu(recyclerView);

    viewModel.getHistory().observe(this, historyModels -> {
      if (historyModels != null) {
        int size = historyModels.size();
        String subtitle = getResources().getQuantityString(R.plurals.history_records, size, size);
        setSubtitle(subtitle);
        adapter.setItems(historyModels);
        boolean historyWarningShown = false;
        if (size == HistoryViewModel.MAX_FREE_RECORDS &&
          !appPreferences.isProVersion() && !historyWarningShown) {
          // TODO show history warning
        }
      }
    });
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == HistoryListAdapter.MENU_ITEM_REMOVE) {
      final int position = adapter.getContextMenuItemPosition();
      final HistoryModel historyModel = adapter.getItem(position);
      viewModel.deleteItem(historyModel);
    } else if (itemId == HistoryListAdapter.MENU_ITEM_ADD_SHORTCUT) {
      if (appPreferences.isProVersion()) {
        final int position = adapter.getContextMenuItemPosition();
        final HistoryModel historyModel = adapter.getItem(position);
        DialogFragment dialog = AddShortcutDialogFragment.newInstance(historyModel);
        dialog.show(getSupportFragmentManager(), AddShortcutDialogFragment.TAG);
      } else {
        new AlertDialog.Builder(this)
          .setTitle(R.string.pro_version_dialog_title)
          .setMessage(R.string.pro_version_unlock_intent_shortcuts)
          .setPositiveButton(R.string.pro_version_get,
            (dialog, which) -> PurchaseActivity.start(this))
          .show();
      }
    }
    return super.onContextItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.history, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_clear_history: {
        new AlertDialog.Builder(this)
          .setTitle(R.string.history_dialog_clear_title)
          .setMessage(R.string.history_dialog_clear_message)
          .setPositiveButton(android.R.string.yes, (dialog, which) -> {
            viewModel.clear();
            finish();
          })
          .setNegativeButton(android.R.string.cancel, null)
          .show();
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
