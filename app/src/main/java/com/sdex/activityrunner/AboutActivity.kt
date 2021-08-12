package com.sdex.activityrunner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.donate.DonateDialog
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import com.sdex.commons.license.LicensesDialogFragment
import com.sdex.commons.util.AppUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {

    override fun getLayout() = R.layout.activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableBackButton()

        versionName.text = getString(
            R.string.about_version_format,
            BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE
        )

        donate.setOnClickListener {
            val dialog = DonateDialog.newInstance()
            dialog.show(supportFragmentManager, DonateDialog.TAG)
        }

        version.setOnClickListener {
            val dialog = DonateDialog.newInstance()
            dialog.show(supportFragmentManager, DonateDialog.TAG)
        }

        source_code.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.REPOSITORY)
        }

        issues_tracker.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.ISSUES_TRACKER)
        }

        openSource.setOnClickListener {
            val dialog = LicensesDialogFragment()
            dialog.show(supportFragmentManager, LicensesDialogFragment.TAG)
        }

        privacyPolicy.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.PP)
        }
    }

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, AboutActivity::class.java)
            context.startActivity(starter)
        }
    }
}
