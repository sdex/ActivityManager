package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sdex.commons.BaseActivity;
import com.sdex.commons.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.psdev.licensesdialog.LicensesDialog;

public class AboutActivity extends BaseActivity {

  @BindView(R.id.version_name)
  TextView versionName;

  public static void start(Context context) {
    Intent starter = new Intent(context, AboutActivity.class);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_about;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    enableBackButton();

    versionName.setText(getString(R.string.about_version_format,
      BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
  }

  @OnClick(R.id.rate_app)
  void rateApp() {
    AppUtils.openPlayStore(this);
  }

  @OnClick(R.id.more_apps)
  void moreApps() {
    AppUtils.openLink(this, AppUtils.DEV_PAGE);
  }

  @OnClick(R.id.donate)
  void donate() {
    PurchaseActivity.start(this);
  }

  @OnClick(R.id.contact)
  void contact() {
    AppUtils.sendEmail(this, AppUtils.ACTIVITY_RUNNER_FEEDBACK_EMAIL,
      AppUtils.ACTIVITY_RUNNER_FEEDBACK_SUBJECT, "");
  }

  @OnClick(R.id.open_source)
  void openSource() {
    new LicensesDialog.Builder(this)
      .setNotices(R.raw.notices)
      .build()
      .show();
  }
}
