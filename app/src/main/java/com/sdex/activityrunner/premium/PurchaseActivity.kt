package com.sdex.activityrunner.premium

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.sdex.activityrunner.R
import com.sdex.activityrunner.extensions.config
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_purchase.*

class PurchaseActivity : BaseActivity() {

  private var appPreferences: AppPreferences? = null
  private var billingClient: BillingClient? = null

  override fun getLayout(): Int {
    return R.layout.activity_purchase
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    appPreferences = AppPreferences(this)

    if (appPreferences!!.isProVersion) {
      purchasePro.visibility = View.GONE
      proActive.visibility = VISIBLE
    }

    billingClient = BillingClient.newBuilder(this)
      .setListener { responseCode, purchases ->
        if (responseCode == BillingResponse.OK && purchases != null) {
          handlePurchases(purchases)
          val snackbar = Snackbar.make(container, R.string.pro_version_done, Snackbar.LENGTH_LONG)
          snackbar.config()
          snackbar.show()
        } else if (responseCode == BillingResponse.USER_CANCELED) {
          // Handle an error caused by a user cancelling the purchase flow.
        } else {
          // Handle any other error codes.
        }
      }
      .build()

    billingClient!!.startConnection(object : BillingClientStateListener {
      override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
        if (billingResponseCode == BillingResponse.OK) {
          val purchasesResult = billingClient!!.queryPurchases(SkuType.INAPP)
          val purchases = purchasesResult.purchasesList
          handlePurchases(purchases)
        }
      }

      override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    })

    get_pro.setOnClickListener {
      val sku = when (amount.checkedRadioButtonId) {
        R.id.sku_2 -> SKU_PRO
        R.id.sku_5 -> SKU_DONATE_5
        R.id.sku_10 -> SKU_DONATE_10
        R.id.sku_20 -> SKU_DONATE_20
        else -> {
          throw IllegalArgumentException()
        }
      }
      showPurchaseDialog(sku)
    }
  }

  private fun handlePurchases(purchases: List<Purchase>) {
    var isProVersionEnabled = false
    for (purchase in purchases) {
      val sku = purchase.sku
      if (isPremiumVersion(sku)) {
        isProVersionEnabled = true

        proActive.visibility = VISIBLE
        purchasePro.visibility = GONE
      }
    }
    appPreferences!!.isProVersion = isProVersionEnabled
  }

  private fun showPurchaseDialog(sku: String) {
    val flowParams = BillingFlowParams.newBuilder()
      .setSku(sku)
      .setType(SkuType.INAPP)
      .build()
    billingClient!!.launchBillingFlow(this, flowParams)
  }

  companion object {

    const val SKU_PRO = "ar_lifetime_pro_2"
    const val SKU_DONATE_5 = "ar_donate_5"
    const val SKU_DONATE_10 = "ar_donate_10"
    const val SKU_DONATE_20 = "ar_donate_20"

    fun start(context: Context) {
      val starter = Intent(context, PurchaseActivity::class.java)
      context.startActivity(starter)
    }

    fun isPremiumVersion(sku: String): Boolean {
      return (SKU_PRO == sku || SKU_DONATE_5 == sku
        || SKU_DONATE_10 == sku || SKU_DONATE_20 == sku)
    }
  }
}
