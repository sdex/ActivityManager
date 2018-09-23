package com.sdex.activityrunner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.activityrunner.util.IntentUtils
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

    privacyPolicy.setOnClickListener {
      IntentUtils.openBrowser(this, AppUtils.ACTIVITY_RUNNER_PP)
    }
  }

  companion object {

    fun start(context: Context) {
      val starter = Intent(context, AboutActivity::class.java)
      context.startActivity(starter)
    }
  }
}
