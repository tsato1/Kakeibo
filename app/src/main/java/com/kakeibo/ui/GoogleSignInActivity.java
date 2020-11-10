package com.kakeibo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.kakeibo.Constants;
import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.billing.BillingClientLifecycle;
import com.kakeibo.settings.SettingsCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GoogleSignInActivity extends AppCompatActivity {
    private static final String TAG = "FakeMainActivity";
    private static final int RC_SIGN_IN = 0;

    private BillingClientLifecycle billingClientLifecycle;

    private FirebaseUserViewModel authenticationViewModel;
    private BillingViewModel billingViewModel;
    private SubscriptionStatusViewModel subscriptionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticationViewModel = ViewModelProviders.of(this).get(FirebaseUserViewModel.class);
        billingViewModel = ViewModelProviders.of(this).get(BillingViewModel.class);
        subscriptionViewModel = ViewModelProviders.of(this).get(SubscriptionStatusViewModel.class);

        billingClientLifecycle = ((SubApp) getApplication()).getBillingClientLifecycle();
        getLifecycle().addObserver(billingClientLifecycle);

        // Register purchases when they change.
        billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });

        // Launch billing flow when user clicks button to buy something.
        billingViewModel.buyEvent.observe(this, new Observer<BillingFlowParams>() {
            @Override
            public void onChanged(BillingFlowParams billingFlowParams) {
                if (billingFlowParams != null) {
                    billingClientLifecycle
                            .launchBillingFlow(GoogleSignInActivity.this, billingFlowParams);
                }
            }
        });

        // Open the Play Store when event is triggered.
        billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sku) {
                Log.i(TAG, "Viewing subscriptions on the Google Play Store");
                String url;
                if (sku == null) {
                    // If the SKU is not specified, just open the Google Play subscriptions URL.
                    url = Constants.PLAY_STORE_SUBSCRIPTION_URL;
                } else {
                    // If the SKU is specified, open the deeplink for this SKU on Google Play.
                    url = String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
                            sku, getApplicationContext().getPackageName());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Update authentication UI.
        final Observer<FirebaseUser> fireaseUserObserver = new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable final FirebaseUser firebaseUser) {
                invalidateOptionsMenu();
                if (firebaseUser == null) {
                    triggerSignIn();
                } else {
                    Log.d(TAG, "Current user: "
                            + firebaseUser.getEmail() + " " + firebaseUser.getDisplayName());
                }
            }
        };
        authenticationViewModel.firebaseUser.observe(this, fireaseUserObserver);

        // Update subscription information when user changes.
        authenticationViewModel.userChangeEvent.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                subscriptionViewModel.userChanged();
                List<Purchase> purchases = billingClientLifecycle.purchaseUpdateEvent.getValue();
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });
    }

    /**
     * Register SKUs and purchase tokens with the server.
     */
    private void registerPurchases(List<Purchase> purchaseList) {
        for (Purchase purchase : purchaseList) {
            String sku = purchase.getSku();
            String purchaseToken = purchase.getPurchaseToken();
            Log.d(TAG, "Register purchase with sku: " + sku + ", token: " + purchaseToken);
            subscriptionViewModel.registerSubscription(sku, purchaseToken);
        }
    }

    /**
     * Sign in with FirebaseUI Auth.
     */
    private void triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!");
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        // Configure the different methods users can sign in
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Sign out with FirebaseUI Auth.
     */
    private void triggerSignOut() {
        subscriptionViewModel.unregisterInstanceId();
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User SIGNED OUT!");
                        authenticationViewModel.updateFirebaseUser();
                    }
                });
    }

    /**
     * Receive Activity result, including sign-in result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If sign-in is successful, update ViewModel.
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Sign-in SUCCESS!");
                authenticationViewModel.updateFirebaseUser();
            } else {
                Log.d(TAG, "Sign-in FAILED!");
            }
        } else {
            Log.e(TAG, "Unrecognized request code: " + requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsCompatActivity.class));
            return true;
        } else if (id == R.id.sign_in) {
            triggerSignIn();
            return true;
        } else if (id == R.id.sign_out) {
            triggerSignOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isSignedIn = authenticationViewModel.isSignedIn();
        menu.findItem(R.id.sign_in).setVisible(!isSignedIn);
        menu.findItem(R.id.sign_out).setVisible(isSignedIn);
        return true;
    }
}
