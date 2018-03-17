package com.sdex.activityrunner.intent.history;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.sdex.activityrunner.R;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AdsHandler;

public class HistoryActivity extends BaseActivity {

  public static final int REQUEST_CODE = 111;

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

    viewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
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
        // TODO confirm
        viewModel.clear();
        finish();
        return true;
      }
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
