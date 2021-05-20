package com.googleplaybilling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

public class SubscriptionActivity extends AppCompatActivity implements PurchasesUpdatedListener{
    private BillingClient billingClient;

    List<String> skuList = new ArrayList<>();

    private final String SUBSCRIBE_VIP = "vip";

    private Button subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        subscribe = findViewById(R.id.subscribe);

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> list) {

            }
        };

        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        if (!userHasSubscribed()){
            subscribe.setVisibility(View.VISIBLE);
        }else {
            subscribe.setVisibility(View.INVISIBLE);
        }

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    skuList.add(SUBSCRIBE_VIP);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(SUBS);
                    billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult,  List<SkuDetails> list) {
                            //process result, check if user already owns the subscription
                            if (!userHasSubscribed()){
                                //launch the purchase flow if user not already owns

                                subscribe.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveSubscriptionState();
                                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(list.get(0)).build();
                                        int responseCode = billingClient.launchBillingFlow(SubscriptionActivity.this, billingFlowParams).getResponseCode();

                                    }
                                });

                            }


                        }
                    });
                }else {
                    Toast.makeText(SubscriptionActivity.this, "error : " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SubscriptionActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private SharedPreferences getPreference() {
        return getApplicationContext().getSharedPreferences("subscribe", 0);
    }
    private SharedPreferences.Editor editPreference() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("subscribe", 0);
        return pref.edit();
    }
    private boolean userHasSubscribed(){
        return getPreference().getBoolean( "subscribe",false);
    }
    private void saveSubscriptionState(){
        editPreference().putBoolean("subscribe", true).commit();
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            subscribe.setVisibility(View.INVISIBLE);

        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.i("purchase cancelled", "purchase cancelled by user");
        } else {
            // Handle any other error codes.
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }



    private void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    saveSubscriptionState();
                    subscribe.setVisibility(View.INVISIBLE);
                    Log.d("purchase token", purchaseToken);
                    //save purchase token in your backend server and verify purchase
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }
}