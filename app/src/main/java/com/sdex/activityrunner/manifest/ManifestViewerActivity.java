package com.sdex.activityrunner.manifest;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;
import com.sdex.activityrunner.R;
import com.sdex.commons.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManifestViewerActivity extends BaseActivity {

  private static final String ARG_PACKAGE_NAME = "arg_package_name";

  @BindView(R.id.highlight_view)
  HighlightJsView highlightJsView;

  public static void start(Context context, String packageName) {
    Intent starter = new Intent(context, ManifestViewerActivity.class);
    starter.putExtra(ARG_PACKAGE_NAME, packageName);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_manifest_viewer;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    highlightJsView.setHighlightLanguage(Language.XML);
    highlightJsView.setTheme(Theme.GITHUB_GIST);
    highlightJsView.setShowLineNumbers(true);
    highlightJsView.setZoomSupportEnabled(true);

    String packageName = getIntent().getStringExtra(ARG_PACKAGE_NAME);

    ViewModelProviders.of(this).get(ManifestViewModel.class)
      .loadManifest(packageName).observe(this, s -> highlightJsView.setSource(s));
  }
}
