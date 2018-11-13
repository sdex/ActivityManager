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
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.commons.BaseActivity
import kotlinx.android.synthetic.main.activity_purchase.*

class PurchaseActivity : BaseActivity() {

  private val appPreferences: AppPreferences by lazy { AppPreferences(this) }
  private var billingClient: BillingClient? = null

  override fun getLayout(): Int {
    return R.layout.activity_purchase
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableBackButton()

    if (appPreferences.isProVersion) {
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
        R.id.sku_2 -> SKU_2
        R.id.sku_5 -> SKU_5
        R.id.sku_10 -> SKU_10
        R.id.sku_20 -> SKU_20
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
    appPreferences.isProVersion = isProVersionEnabled
  }

  private fun showPurchaseDialog(sku: String) {
    val flowParams = BillingFlowParams.newBuilder()
      .setSku(sku)
      .setType(SkuType.INAPP)
      .build()
    billingClient!!.launchBillingFlow(this, flowParams)
  }

  companion object {

    const val SKU_2 = "am_usd_2"
    const val SKU_5 = "am_usd_5"
    const val SKU_10 = "am_usd_10"
    const val SKU_20 = "am_usd_20"

    fun start(context: Context) {
      val starter = Intent(context, PurchaseActivity::class.java)
      context.startActivity(starter)
    }

    fun isPremiumVersion(sku: String): Boolean {
      return (SKU_2 == sku || SKU_5 == sku
        || SKU_10 == sku || SKU_20 == sku)
    }
  }
}
