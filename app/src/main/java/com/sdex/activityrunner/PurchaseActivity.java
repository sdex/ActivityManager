package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AppPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchaseActivity extends BaseActivity {

  public static final String SKU_PRO = "ar_lifetime_pro_2";
  public static final String SKU_DONATE_5 = "ar_donate_5";
  public static final String SKU_DONATE_10 = "ar_donate_10";
  public static final String SKU_DONATE_20 = "ar_donate_20";

  @BindView(R.id.price)
  TextView priceView;

  private AppPreferences appPreferences;
  private BillingClient billingClient;

  public static void start(Context context) {
    Intent starter = new Intent(context, PurchaseActivity.class);
    context.startActivity(starter);
  }

  @Override
  protected int getLayout() {
    return R.layout.activity_purchase;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    enableBackButton();

    appPreferences = new AppPreferences(this);
    if (appPreferences.isProVersion()) {
      findViewById(R.id.purchase_pro).setVisibility(View.GONE);
    }

    billingClient = BillingClient.newBuilder(this)
      .setListener((responseCode, purchases) -> {
        if (responseCode == BillingResponse.OK && purchases != null) {
          handlePurchases(purchases);

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
      })
      .build();
    billingClient.startConnection(new BillingClientStateListener() {
      @Override
      public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
        if (billingResponseCode == BillingResponse.OK) {
          PurchasesResult purchasesResult = billingClient.queryPurchases(SkuType.INAPP);
          List<Purchase> purchases = purchasesResult.getPurchasesList();
          handlePurchases(purchases);
          fetchPrice();
        }
      }

      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    });
  }

  private void fetchPrice() {
    List<String> skuList = new ArrayList<>();
    skuList.add(SKU_PRO);
    skuList.add(SKU_DONATE_5);
    skuList.add(SKU_DONATE_10);
    skuList.add(SKU_DONATE_20);
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(SkuType.INAPP);
    billingClient.querySkuDetailsAsync(params.build(), (responseCode, skuDetailsList) -> {
      if (responseCode == BillingResponse.OK && skuDetailsList != null) {
        for (SkuDetails skuDetails : skuDetailsList) {
          String sku = skuDetails.getSku();
          String price = skuDetails.getPrice();
          if (SKU_PRO.equals(sku)) {
            priceView.setText(price);
          }
        }
      }
    });
  }

  private void handlePurchases(List<Purchase> purchases) {
    boolean isProVersionEnabled = false;
    for (Purchase purchase : purchases) {
      String sku = purchase.getSku();
      if (isPremiumVersion(sku)) {
        isProVersionEnabled = true;
      }
      if (SKU_DONATE_5.equals(sku)) {
        findViewById(R.id.donate_5_usd).setVisibility(View.GONE);
      } else if (SKU_DONATE_10.equals(sku)) {
        findViewById(R.id.donate_10_usd).setVisibility(View.GONE);
      } else if (SKU_DONATE_20.equals(sku)) {
        findViewById(R.id.donate_20_usd).setVisibility(View.GONE);
      }
    }
    appPreferences.setProVersion(isProVersionEnabled);
  }

  public static boolean isPremiumVersion(String sku) {
    return SKU_PRO.equals(sku) || SKU_DONATE_5.equals(sku)
      || SKU_DONATE_10.equals(sku) || SKU_DONATE_20.equals(sku);
  }

  void showPurchaseDialog(String sku) {
    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
      .setSku(sku)
      .setType(SkuType.INAPP)
      .build();
    billingClient.launchBillingFlow(this, flowParams);
  }

  @OnClick(R.id.get_pro)
  void purchase() {
    showPurchaseDialog(SKU_PRO);
  }

  @OnClick(R.id.donate_5_usd)
  void donate5() {
    showPurchaseDialog(SKU_DONATE_5);
  }

  @OnClick(R.id.donate_10_usd)
  void donate10() {
    showPurchaseDialog(SKU_DONATE_10);
  }

  @OnClick(R.id.donate_20_usd)
  void donate20() {
    showPurchaseDialog(SKU_DONATE_20);
  }
}
