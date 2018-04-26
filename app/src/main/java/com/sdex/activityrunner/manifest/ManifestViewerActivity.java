package com.sdex.activityrunner.manifest;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;

import com.pddstudio.highlightjs.HighlightJsView;
import com.pddstudio.highlightjs.models.Language;
import com.pddstudio.highlightjs.models.Theme;
import com.sdex.activityrunner.R;
import com.sdex.commons.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManifestViewerActivity extends BaseActivity {

  private static final String ARG_PACKAGE_NAME = "arg_package_name";
  private static final String ARG_NAME = "arg_name";

  @BindView(R.id.highlight_view)
  HighlightJsView highlightJsView;
  @BindView(R.id.progress)
  ContentLoadingProgressBar progressBar;

  public static void start(Context context, String packageName, String name) {
    Intent starter = new Intent(context, ManifestViewerActivity.class);
    starter.putExtra(ARG_PACKAGE_NAME, packageName);
    starter.putExtra(ARG_NAME, name);
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
    enableBackButton();

    progressBar.show();

    highlightJsView.setHighlightLanguage(Language.XML);
    highlightJsView.setTheme(Theme.GITHUB_GIST);
    highlightJsView.setShowLineNumbers(true);
    highlightJsView.setZoomSupportEnabled(true);
    highlightJsView.setOnContentChangedListener(() -> {
      // hide progress
    });

    String packageName = getIntent().getStringExtra(ARG_PACKAGE_NAME);
    String name = getIntent().getStringExtra(ARG_NAME);

    if (packageName == null) {
      return;
    }

    setTitle(name);

    ViewModelProviders.of(this).get(ManifestViewModel.class)
      .loadManifest(packageName).observe(this, s -> {
      highlightJsView.setSource(s);
      progressBar.hide();
    });
  }
}
