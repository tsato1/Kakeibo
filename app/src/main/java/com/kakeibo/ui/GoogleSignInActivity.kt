package com.kakeibo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.android.billingclient.api.Purchase
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.settings.SettingsActivity
import com.kakeibo.ui.viewmodel.BillingViewModel
import com.kakeibo.ui.viewmodel.FirebaseUserViewModel
import com.kakeibo.ui.viewmodel.SubscriptionStatusViewModel

class GoogleSignInActivity : AppCompatActivity() {
    private lateinit var billingClientLifecycle: BillingClientLifecycle

    private lateinit var authenticationViewModel: FirebaseUserViewModel
    private lateinit var billingViewModel: BillingViewModel
    private lateinit var subscriptionViewModel: SubscriptionStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
        billingViewModel = ViewModelProviders.of(this)[BillingViewModel::class.java]
        subscriptionViewModel = ViewModelProviders.of(this)[SubscriptionStatusViewModel::class.java]
        billingClientLifecycle = (application as SubApp).billingClientLifecycle
        lifecycle.addObserver(billingClientLifecycle)

        // Register purchases when they change.
        billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            it?.let {
                registerPurchases(it)
            }
        })

        // Launch billing flow when user clicks button to buy something.
        billingViewModel.buyEvent.observe(this, {
            it?.let {
                billingClientLifecycle.launchBillingFlow(this, it)
            }
        })

        // Open the Play Store when event is triggered.
        billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, {
            Log.i(TAG, "Viewing subscriptions on the Google Play Store")

            val sku = it
            val url = if (sku == null) {
                // If the SKU is not specified, just open the Google Play subscriptions URL.
                Constants.PLAY_STORE_SUBSCRIPTION_URL
            } else {
                // If the SKU is specified, open the deeplink for this SKU on Google Play.
                String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL, sku, packageName)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        })

        // Update authentication UI.
//        val fireaseUserObserver: Observer<FirebaseUser> = Observer { firebaseUser ->
//            invalidateOptionsMenu()
//            if (firebaseUser == null) {
//                triggerSignIn()
//            } else {
//                Log.d(TAG, "Current user: "
//                        + firebaseUser.email + " " + firebaseUser.displayName)
//            }
//        }

        authenticationViewModel.firebaseUser.observe(this, {
            invalidateOptionsMenu()
            if (it == null) {
                triggerSignIn()
            } else {
                Log.d(TAG, "CURRENT user: " + it.email + " " + it.displayName)
            }
        })

        // Update subscription information when user changes.
        authenticationViewModel.userChangeEvent.observe(this, {
            subscriptionViewModel.userChanged()
            billingClientLifecycle.purchaseUpdateEvent.value?.let {
                registerPurchases(it)
            }
        })
    }

    /**
     * Register SKUs and purchase tokens with the server.
     */
    private fun registerPurchases(purchaseList: List<Purchase>) {
        for (purchase in purchaseList) {
            val sku = purchase.sku
            val purchaseToken = purchase.purchaseToken
            Log.d(TAG, "Register purchase with sku: $sku, token: $purchaseToken")
            subscriptionViewModel.registerSubscription(sku, purchaseToken)
        }
    }

    /**
     * Sign in with FirebaseUI Auth.
     */
    private fun triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!")
        val providers = listOf(EmailBuilder().build(), GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    /**
     * Sign out with FirebaseUI Auth.
     */
    private fun triggerSignOut() {
        subscriptionViewModel.unregisterInstanceId()
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    Log.d(TAG, "User SIGNED OUT!")
                    authenticationViewModel.updateFirebaseUser()
                }
    }

    /**
     * Receive Activity result, including sign-in result.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                // If sign-in is successful, update ViewModel.
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Sign-in SUCCESS!")
                    authenticationViewModel.updateFirebaseUser()
                } else {
                    Log.d(TAG, "Sign-in FAILED!")
                }
            }
            else -> {
                Log.e(TAG, "Unrecognized request code: $requestCode")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.sign_in -> {
                triggerSignIn()
                return true
            }
            R.id.sign_out -> {
                triggerSignOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isSignedIn = authenticationViewModel.isSignedIn()
        menu.findItem(R.id.sign_in).isVisible = !isSignedIn
        menu.findItem(R.id.sign_out).isVisible = isSignedIn
        return true
    }

    companion object {
        private const val TAG = "FakeMainActivity"
        private const val RC_SIGN_IN = 0
    }
}