package com.sdex.activityrunner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.databinding.ActivityAboutBinding
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.commons.BaseActivity
import com.sdex.commons.license.LicensesDialogFragment
import com.sdex.commons.util.AppUtils

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        binding.versionName.text = getString(
            R.string.about_version_format,
            BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE
        )

        binding.donate.setOnClickListener {
            AppUtils.openLink(this, getString(R.string.donate_link))
        }

        binding.sourceCode.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.REPOSITORY)
        }

        binding.issuesTracker.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.ISSUES_TRACKER)
        }

        binding.openSource.setOnClickListener {
            LicensesDialogFragment()
                .show(supportFragmentManager, LicensesDialogFragment.TAG)
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}
