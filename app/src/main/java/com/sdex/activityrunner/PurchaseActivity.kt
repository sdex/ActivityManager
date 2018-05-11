package com.sdex.activityrunner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.sdex.activityrunner.extensions.enableBackButton
import com.sdex.commons.BaseActivity
import com.sdex.commons.ads.AppPreferences
import kotlinx.android.synthetic.main.activity_purchase.*
import java.util.*

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
    }

    billingClient = BillingClient.newBuilder(this)
      .setListener { responseCode, purchases ->
        if (responseCode == BillingResponse.OK && purchases != null) {
          handlePurchases(purchases)

          // do not consume donation
          //          for (Purchase purchase : purchases) {
          //            String sku = purchase.getSku();
          //            if (!SKU_PRO.equals(sku)) { // consume donate
          //              billingClient.consumeAsync(purchase.getPurchaseToken(),
          //                (responseCode1, purchaseToken) -> {
          //                });
          //            }
          //          }

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
          fetchPrice()
        }
      }

      override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    })

    get_pro.setOnClickListener {
      showPurchaseDialog(SKU_PRO)
    }
    donate_5_usd.setOnClickListener {
      showPurchaseDialog(SKU_DONATE_5)
    }
    donate_10_usd.setOnClickListener {
      showPurchaseDialog(SKU_DONATE_10)
    }
    donate_20_usd.setOnClickListener {
      showPurchaseDialog(SKU_DONATE_20)
    }
  }

  private fun fetchPrice() {
    val skuList = ArrayList<String>()
    skuList.add(SKU_PRO)
    skuList.add(SKU_DONATE_5)
    skuList.add(SKU_DONATE_10)
    skuList.add(SKU_DONATE_20)
    val params = SkuDetailsParams.newBuilder()
    params.setSkusList(skuList).setType(SkuType.INAPP)
    billingClient!!.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
      if (responseCode == BillingResponse.OK && skuDetailsList != null) {
        for (skuDetails in skuDetailsList) {
          val sku = skuDetails.sku
          val price = skuDetails.price
          if (SKU_PRO == sku) {
            priceView.text = price
          }
        }
      }
    }
  }

  private fun handlePurchases(purchases: List<Purchase>) {
    var isProVersionEnabled = false
    for (purchase in purchases) {
      val sku = purchase.sku
      if (isPremiumVersion(sku)) {
        isProVersionEnabled = true
      }
      when {
        SKU_DONATE_5 == sku -> donate_5_usd.visibility = View.GONE
        SKU_DONATE_10 == sku -> donate_10_usd.visibility = View.GONE
        SKU_DONATE_20 == sku -> donate_20_usd.visibility = View.GONE
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
