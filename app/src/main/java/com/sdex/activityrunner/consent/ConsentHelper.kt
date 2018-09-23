package com.sdex.activityrunner.consent

import android.app.Activity
import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentStatus
import com.sdex.commons.util.AppUtils
import java.net.MalformedURLException
import java.net.URL

class ConsentHelper {

  private var consentForm: ConsentForm? = null

  fun showForm(activity: Activity) {
    var privacyUrl: URL? = null
    try {
      privacyUrl = URL(AppUtils.ACTIVITY_RUNNER_PP)
    } catch (e: MalformedURLException) {
      e.printStackTrace()
    }

    consentForm = ConsentForm.Builder(activity, privacyUrl)
      .withListener(object : ConsentFormListener() {
        override fun onConsentFormLoaded() {
          if (!activity.isFinishing) {
            consentForm?.show()
          }
        }

        override fun onConsentFormOpened() {
        }

        override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
          if (userPrefersAdFree!!) {
            // TODO This is where you write your Intent to launch the purchase flow dialog
          } else {
            when (consentStatus) {
              ConsentStatus.PERSONALIZED -> {//adManager.updatePersonalized()
              }
              ConsentStatus.NON_PERSONALIZED -> {
                //adManager.updateNonPersonalized()
              }
              ConsentStatus.UNKNOWN -> {
              }

            }
          }
        }

        override fun onConsentFormError(errorDescription: String?) {
        }
      })
      .withPersonalizedAdsOption()
      .withNonPersonalizedAdsOption()
      .build()

    consentForm!!.load()
  }
}
