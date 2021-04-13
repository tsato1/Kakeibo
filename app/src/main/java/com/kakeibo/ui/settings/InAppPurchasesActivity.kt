package com.kakeibo.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.databinding.ActivityInAppPurchaseBinding
import com.kakeibo.ui.viewmodel.BillingViewModel

class InAppPurchasesActivity : AppCompatActivity() {

    companion object {
        private val TAG = InAppPurchasesActivity::class.simpleName
    }

    private lateinit var binding: ActivityInAppPurchaseBinding
    private lateinit var billingClientLifecycle: BillingClientLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInAppPurchaseBinding.inflate(layoutInflater)
        val billingViewModel = ViewModelProvider(this).get(BillingViewModel::class.java)
        binding.billingViewModel = billingViewModel

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

        val view = binding.root
        setContentView(view)
    }


}