package com.sdex.commons;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
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

  public void setSubtitle(CharSequence subtitle) {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setSubtitle(subtitle);
    }
  }

  protected void enableBackButton() {
    ActionBar supportActionBar = getSupportActionBar();
    if (supportActionBar != null) {
      supportActionBar.setDisplayHomeAsUpEnabled(true);
    }
  }
}
