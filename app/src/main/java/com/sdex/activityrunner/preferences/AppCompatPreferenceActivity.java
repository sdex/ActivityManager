package com.sdex.activityrunner.preferences;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sdex.activityrunner.R;

public abstract class AppCompatPreferenceActivity extends PreferenceActivity {

  private AppCompatDelegate mDelegate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getDelegate().installViewFactory();
    getDelegate().onCreate(savedInstanceState);
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupActionBar() {
    ViewGroup root = (ViewGroup) findViewById(android.R.id.list)
      .getParent().getParent().getParent();
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    Toolbar toolbar = (Toolbar) layoutInflater.inflate(R.layout.toolbar, root, false);
    root.addView(toolbar, 0);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    getDelegate().onPostCreate(savedInstanceState);
  }

  public ActionBar getSupportActionBar() {
    return getDelegate().getSupportActionBar();
  }

  public void setSupportActionBar(@Nullable Toolbar toolbar) {
    getDelegate().setSupportActionBar(toolbar);
  }

  @NonNull
  @Override
  public MenuInflater getMenuInflater() {
    return getDelegate().getMenuInflater();
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    getDelegate().setContentView(layoutResID);
  }

  @Override
  public void setContentView(View view) {
    getDelegate().setContentView(view);
  }

  @Override
  public void setContentView(View view, ViewGroup.LayoutParams params) {
    getDelegate().setContentView(view, params);
  }

  @Override
  public void addContentView(View view, ViewGroup.LayoutParams params) {
    getDelegate().addContentView(view, params);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    getDelegate().onPostResume();
  }

  @Override
  protected void onTitleChanged(CharSequence title, int color) {
    super.onTitleChanged(title, color);
    getDelegate().setTitle(title);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    getDelegate().onConfigurationChanged(newConfig);
  }

  @Override
  protected void onStop() {
    super.onStop();
    getDelegate().onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    getDelegate().onDestroy();
  }

  public void invalidateOptionsMenu() {
    getDelegate().invalidateOptionsMenu();
  }

  private AppCompatDelegate getDelegate() {
    if (mDelegate == null) {
      mDelegate = AppCompatDelegate.create(this, null);
    }
    return mDelegate;
  }
}
