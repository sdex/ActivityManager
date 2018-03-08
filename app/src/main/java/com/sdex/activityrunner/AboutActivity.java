package com.sdex.activityrunner;

import android.os.Bundle;
import android.view.Menu;
import com.sdex.commons.BaseActivity;

public class AboutActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    enableBackButton();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }
}
