package com.sdex.activityrunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.sdex.commons.BaseActivity;
import com.sdex.commons.ads.AppPreferences;
import java.util.List;

public class PurchaseActivity extends BaseActivity {

  public static final String SKU_PRO = "ar_lifetime_pro_2";

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
    enableBackButton();

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
        }
      }

      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    });

    findViewById(R.id.get_pro).setOnClickListener(v -> purchase());
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
