package com.sdex.activityrunner.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sdex.activityrunner.R
import com.sdex.activityrunner.commons.BaseActivity
import com.sdex.activityrunner.databinding.ActivityAboutBinding
import com.sdex.activityrunner.util.AppUtils
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.PackageInfoProvider
import com.sdex.activityrunner.util.PackageInfoProvider.Companion.getVersionCodeCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity : BaseActivity() {

    @Inject
    lateinit var packageInfoProvider: PackageInfoProvider
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(isBackButtonEnabled = true)

        val packageInfo = packageInfoProvider.getPackageInfo(packageName)

        binding.versionName.text = getString(
            R.string.app_version_format,
            packageInfo.versionName,
            packageInfo.getVersionCodeCompat(),
        )

        binding.donate.setOnClickListener {
            AppUtils.openLink(this, getString(R.string.donate_link))
        }

        binding.issuesTracker.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.ISSUES_TRACKER)
        }

        binding.suggestIdea.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.SUGGESTION_LINK)
        }

        binding.translate.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.TRANSLATE_LINK)
        }

        binding.sourceCode.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.REPOSITORY)
        }

        binding.openSource.setOnClickListener {
            LicensesDialogFragment()
                .show(supportFragmentManager, LicensesDialogFragment.TAG)
        }

        binding.version.setOnClickListener {
            IntentUtils.openBrowser(this, AppUtils.CHANGELOG)
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}
