package com.sdex.activityrunner;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

public class OreoPackageManagerBugActivity extends FragmentActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_oreo_package_manager_bug);

    findViewById(R.id.button2).setOnClickListener(v -> finish());

    findViewById(R.id.button3).setOnClickListener(v -> {
      finish();
      try {
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
