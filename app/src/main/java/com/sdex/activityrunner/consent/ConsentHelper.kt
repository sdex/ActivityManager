package com.sdex.activityrunner.consent

import android.app.Activity
import com.google.ads.consent.*
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.premium.PurchaseActivity
import com.sdex.commons.ads.AppPreferences
import com.sdex.commons.util.AppUtils
import java.net.MalformedURLException
import java.net.URL

class ConsentHelper {

  private var consentForm: ConsentForm? = null

  fun handleConsent(activity: Activity) {
    if (BuildConfig.DEBUG) {
      ConsentInformation.getInstance(activity)
        .addTestDevice("5104CBC1CE73275F9F34E7A2A90A090B")
      ConsentInformation.getInstance(activity)
        .debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
    }

    val consentInformation = ConsentInformation.getInstance(activity)
    val publisherIds = arrayOf(activity.getString(R.string.ad_publisher_id))
    consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
      override fun onFailedToUpdateConsentInfo(reason: String?) {
      }

      override fun onConsentInfoUpdated(consentStatus: ConsentStatus?) {
        if (consentStatus == ConsentStatus.UNKNOWN) {
          val consentHelper = ConsentHelper()
          consentHelper.showForm(activity)
        }
      }

    })
  }

  private fun showForm(activity: Activity) {
    var privacyUrl: URL? = null
    try {
      privacyUrl = URL(AppUtils.ACTIVITY_RUNNER_PP)
    } catch (e: MalformedURLException) {
      e.printStackTrace()
    }

    val prefs = AppPreferences(activity)

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
            PurchaseActivity.start(activity)
          } else {
            when (consentStatus) {
              ConsentStatus.PERSONALIZED -> {
                prefs.isAdsPersonalized = true
              }
              ConsentStatus.NON_PERSONALIZED -> {
                prefs.isAdsPersonalized = false
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
      .withAdFreeOption()
      .build()

    consentForm!!.load()
  }
}
