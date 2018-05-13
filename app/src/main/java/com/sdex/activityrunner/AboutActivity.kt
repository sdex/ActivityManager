package com.sdex.activityrunner

import android.os.Bundle
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.commons.BaseActivity
import com.sdex.commons.util.AppUtils
import de.psdev.licensesdialog.LicensesDialog
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {

  override fun getLayout(): Int {
    return R.layout.activity_about
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    versionName.text = getString(R.string.about_version_format,
      BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

    rateApp.setOnClickListener {
      AppUtils.openPlayStore(this)
    }

    moreApps.setOnClickListener {
      AppUtils.openLink(this, AppUtils.DEV_PAGE)
    }

    donate.setOnClickListener {
      PurchaseActivity.start(this)
    }

    contact.setOnClickListener {
      AppUtils.sendEmail(this, AppUtils.ACTIVITY_RUNNER_FEEDBACK_EMAIL,
        AppUtils.ACTIVITY_RUNNER_FEEDBACK_SUBJECT, "")
    }

    openSource.setOnClickListener {
      LicensesDialog.Builder(this)
        .setNotices(R.raw.notices)
        .build()
        .show()
    }
  }
}
