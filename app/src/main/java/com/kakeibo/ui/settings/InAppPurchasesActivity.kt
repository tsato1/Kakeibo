package com.kakeibo.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.Purchase
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.databinding.ActivityInAppPurchaseBinding
import com.kakeibo.ui.viewmodel.BillingViewModel
import com.kakeibo.ui.viewmodel.FirebaseUserViewModel
import com.kakeibo.ui.viewmodel.KkbAppViewModel
import com.kakeibo.ui.viewmodel.SubscriptionViewModel

class InAppPurchasesActivity : AppCompatActivity() {

    companion object {
        private val TAG = InAppPurchasesActivity::class.simpleName
        private const val RC_SIGN_IN = 0
    }

    private lateinit var _binding: ActivityInAppPurchaseBinding
    private lateinit var _billingClientLifecycle: BillingClientLifecycle
    private lateinit var _startForResult: ActivityResultLauncher<Intent>

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private val _authenticationViewModel: FirebaseUserViewModel by viewModels()
    private val _billingViewModel: BillingViewModel by viewModels()
    private val _subscriptionViewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityInAppPurchaseBinding.inflate(layoutInflater)
        val billingViewModel = ViewModelProvider(this).get(BillingViewModel::class.java)
        _binding.billingViewModel = billingViewModel
        setContentView(_binding.root)

        _kkbAppViewModel.all.observe(this, {
            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (showAds) {
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.visibility = View.VISIBLE
            }
        })

        _billingClientLifecycle = (application as SubApp).billingClientLifecycle
        lifecycle.addObserver(_billingClientLifecycle)

        /* Register purchases when they change. */
        _billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            it?.let {
                registerPurchases(it)
            }
        })

        /* Launch billing flow when user clicks button to buy something. */
        _billingViewModel.buyEvent.observe(this, {
            it?.let {
                _billingClientLifecycle.launchBillingFlow(this@InAppPurchasesActivity, it)
            }
        })

        /* Open the Play Store when event is triggered. */
        _billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, {
            Log.i(TAG, "Viewing subscriptions on the Google Play Store")
            val sku = it
            val url = if (sku == null) {
                /* If the SKU is not specified, just open the Google Play subscriptions URL. */
                Constants.PLAY_STORE_SUBSCRIPTION_URL
            } else {
                /* If the SKU is specified, open the deeplink for this SKU on Google Play. */
                String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL, sku, packageName)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        })

        /* Update subscription information when user changes. */
        _authenticationViewModel.userChangeEvent.observe(this, {
            _subscriptionViewModel.userChanged()
            _billingClientLifecycle.purchaseUpdateEvent.value?.let {
                registerPurchases(it)
            }
        })

        val profileImageView = _binding.imvUserProfile
        val nameTextView = _binding.txvUserName
        val emailTextView = _binding.txvUserEmail

        _authenticationViewModel.firebaseUser.observe(this, {
            if (it == null) {
                triggerSignIn()
            } else {
                Glide.with(this)
                    .load(it.photoUrl)
                    .circleCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(profileImageView)
                nameTextView.text = it.displayName
                emailTextView.text = it.email
            }
        })

        _startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_LONG).show()
                _authenticationViewModel.updateFirebaseUser()
            } else {
                Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_LONG).show()
            }
        }

        _binding.btnBuyBasic.setOnClickListener(ButtonClickListener())
        _binding.btnBuyPremium.setOnClickListener(ButtonClickListener())
    }

    /*
     * Register SKUs and purchase tokens with the server.
     */
    private fun registerPurchases(purchaseList: List<Purchase>) {
        for (purchase in purchaseList) {
            val sku = purchase.skus[0]
            val purchaseToken = purchase.purchaseToken
            Log.d(TAG, "Register purchase with sku: $sku, token: $purchaseToken")
            _subscriptionViewModel.registerSubscription(
                sku = sku,
                purchaseToken = purchaseToken
            )
        }
    }
//    private fun registerPurchases(purchaseList: List<Purchase>) {
//        for (purchase in purchaseList) {
//            val sku = purchase.sku
//            val purchaseToken = purchase.purchaseToken
//            Log.d(TAG, "Register purchase with sku: $sku, token: $purchaseToken")
//            _subscriptionViewModel.registerSubscription(sku, purchaseToken)
//        }
//    }


    /*
     * Sign in with FirebaseUI Auth.
     */
    private fun triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!")
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
//        startActivityForResult(
//            AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build(),
//            RC_SIGN_IN)
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        _startForResult.launch(intent)
    }
//    private fun triggerSignIn() {
//        val providers: MutableList<AuthUI.IdpConfig> = ArrayList()
//        providers.add(AuthUI.IdpConfig.EmailBuilder().build())
//        providers.add(AuthUI.IdpConfig.GoogleBuilder().build())
//        startActivityForResult(
//            AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build(),
//            RC_SIGN_IN
//        )
//    }

    /*
     * Receive Activity result, including sign-in result.
     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (requestCode) {
//            RC_SIGN_IN -> {
//                if (resultCode == RESULT_OK) {
//                    Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_LONG).show()
//                    _authenticationViewModel.updateFirebaseUser()
//                    return
//                } else {
//                    Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_LONG).show()
//                }
//            }
//            else -> {
//                Log.e(TAG, "Unrecognized request code: $requestCode")
//            }
//        }
//
//        if (!_authenticationViewModel.isSignedIn()) finish()
//    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            if (!_authenticationViewModel.isSignedIn()) {
                Toast.makeText(applicationContext, "Please sign-in first.", Toast.LENGTH_LONG).show()
                return
            }
        }
    }
}