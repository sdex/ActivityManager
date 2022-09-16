package com.sdex.activityrunner

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import org.acra.ReportField
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber

@HiltAndroidApp
class ActivityManagerApplication : Application() {

    private val appPreferences by lazy { AppPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appPreferences.onAppOpened()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            reportContent = listOf(
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.STACK_TRACE,
            )
            mailSender {
                mailTo = "activitymanagerapp@gmail.com"
                subject = "Activity Manager crash report"
            }
            dialog {
                title = "Crash report"
                text = "App crashed. Do you want to send the email report?"
                resTheme = R.style.AppDialogTheme
                positiveButtonText = "Yes"
                negativeButtonText = "No"
            }
        }
    }
}
