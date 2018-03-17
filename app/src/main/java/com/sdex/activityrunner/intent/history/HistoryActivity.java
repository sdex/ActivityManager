package com.sdex.activityrunner.intent.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.intent.history.HistoryListAdapter.Callback;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;

public class HistoryActivity extends BaseActivity {

  public static final int REQUEST_CODE = 111;

  @BindView(R.id.list)
  RecyclerView recyclerView;

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

    FrameLayout adsContainer = findViewById(R.id.ads_container);
    AdsHandler adsHandler = new AdsHandler(this, adsContainer);
    adsHandler.init(this, R.string.ad_banner_unit_id);

    enableBackButton();

    adapter = new HistoryListAdapter(new Callback() {
      @Override
      public void onItemClicked(int position) {
        Intent data = new Intent();
        // TODO return item
//        setResult(RESULT_OK, );
        finish();
      }

      @Override
      public boolean onItemLongClicked(int position) {
        // TODO show dialog delete
        return false;
      }
    });
    adapter.setHasStableIds(true);
    recyclerView.setAdapter(adapter);

    viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);

    viewModel.getHistory().observe(this,
      historyModels -> adapter.setItems(historyModels));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.history, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_clear_history: {
        new AlertDialog.Builder(this)
          .setTitle("Clear history")
          .setMessage("Are you sure?")
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
