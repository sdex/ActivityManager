package com.sdex.commons;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.sdex.commons.util.AppUtils;

public abstract class BaseActivity extends AppCompatActivity {

  @LayoutRes
  protected abstract int getLayout();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayout());
    Toolbar toolbar = findViewById(R.id.toolbar);
    if (toolbar != null) {
      setSupportActionBar(toolbar);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.sdex_commons_base_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
    } else if (itemId == R.id.rate) {
      AppUtils.openPlayStore(this);
      return true;
    } else if (itemId == R.id.more_apps) {
      AppUtils.openLink(this, AppUtils.DEV_PAGE);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void setTitle(CharSequence title) {
    super.setTitle(title);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(title);
    }
  }

  protected void enableBackButton() {
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }
}
