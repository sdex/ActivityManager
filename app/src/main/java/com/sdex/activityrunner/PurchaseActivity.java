package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
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

public class PurchaseActivity extends BaseActivity {

  public static final String SKU_PRO = "ar_lifetime_pro_2";

  private AppPreferences appPreferences;
  private BillingClient billingClient;
  private Button purchase;

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
    enableBackButton();

    purchase = findViewById(R.id.get_pro);
    purchase.setOnClickListener(v -> purchase());

    appPreferences = new AppPreferences(this);
    billingClient = BillingClient.newBuilder(this)
      .setListener((responseCode, purchases) -> {
        if (responseCode == BillingResponse.OK && purchases != null) {
          handlePurchases(purchases);
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
//          fetchPrice();
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
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(SkuType.INAPP);
    billingClient.querySkuDetailsAsync(params.build(), (responseCode, skuDetailsList) -> {
      if (responseCode == BillingResponse.OK && skuDetailsList != null) {
        for (SkuDetails skuDetails : skuDetailsList) {
          String sku = skuDetails.getSku();
          String price = skuDetails.getPrice();
          if (SKU_PRO.equals(sku)) {
            purchase.append(" " + price);
          }
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  private void handlePurchases(List<Purchase> purchases) {
    boolean isProVersionEnabled = false;
    for (Purchase purchase : purchases) {
      if (SKU_PRO.equals(purchase.getSku())) {
        isProVersionEnabled = true;
        invalidateOptionsMenu();
      }
    }

    appPreferences.setProVersion(isProVersionEnabled);
  }

  private void purchase() {
    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
      .setSku(SKU_PRO)
      .setType(SkuType.INAPP)
      .build();
    billingClient.launchBillingFlow(this, flowParams);
  }
}
