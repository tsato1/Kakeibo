package com.kakeibo.billing

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import com.kakeibo.Constants
import com.kakeibo.ui.SingleLiveEvent
import java.util.*

class BillingClientLifecycle private constructor(private val app: Application) : LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener {
    /**
     * The purchase event is observable. Only one observer will be notified.
     */
    var purchaseUpdateEvent = SingleLiveEvent<List<Purchase>?>()

    /**
     * Purchases are observable. This list will be updated when the Billing Library
     * detects new or existing purchases. All observers will be notified.
     */
    var purchases = MutableLiveData<List<Purchase>?>()

    /**
     * SkuDetails for all known SKUs.
     */
    var skusWithSkuDetails = MutableLiveData<Map<String, SkuDetails>>()
    private var billingClient: BillingClient? = null
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        Log.d(TAG, "ON_CREATE")
        // Create a new BillingClient in onCreate().
        // Since the BillingClient can only be used once, we need to create a new instance
        // after ending the previous connection to the Google Play Store in onDestroy().
        billingClient = BillingClient.newBuilder(app)
                .setListener(this)
                .enablePendingPurchases() // Not used for subscriptions.
                .build()
        if (!billingClient!!.isReady) {
            Log.d(TAG, "BillingClient: Start connection...")
            billingClient!!.startConnection(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        Log.d(TAG, "ON_DESTROY")
        if (billingClient!!.isReady) {
            Log.d(TAG, "BillingClient can only be used once -- closing connection")
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            billingClient!!.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.
            querySkuDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
        // TODO: Try connecting again with exponential backoff.
    }

    /**
     * Receives the result from [.querySkuDetails]}.
     *
     *
     * Store the SkuDetails and post them in the [.skusWithSkuDetails]. This allows other
     * parts of the app to use the [SkuDetails] to show SKU information and make purchases.
     */
    override fun onSkuDetailsResponse(billingResult: BillingResult, skuDetailsList: List<SkuDetails>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                if (skuDetailsList == null) {
                    Log.w(TAG, "onSkuDetailsResponse: null SkuDetails list")
                    skusWithSkuDetails.postValue(emptyMap())
                } else {
                    val newSkusDetailList: MutableMap<String, SkuDetails> = HashMap()
                    for (skuDetails in skuDetailsList) {
                        newSkusDetailList[skuDetails.sku] = skuDetails
                    }
                    skusWithSkuDetails.postValue(newSkusDetailList)
                    Log.i(TAG, "onSkuDetailsResponse: count " + newSkusDetailList.size)
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED, BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE, BillingClient.BillingResponseCode.BILLING_UNAVAILABLE, BillingClient.BillingResponseCode.ITEM_UNAVAILABLE, BillingClient.BillingResponseCode.DEVELOPER_ERROR, BillingClient.BillingResponseCode.ERROR -> Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED, BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED, BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
            else -> Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
        }
    }

    /**
     * Query Google Play Billing for existing purchases.
     *
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    fun queryPurchases() {
        if (!billingClient!!.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
        }
        Log.d(TAG, "queryPurchases: SUBS")
        val result = billingClient!!.queryPurchases(BillingClient.SkuType.SUBS)
        if (result.purchasesList == null) {
            Log.i(TAG, "queryPurchases: null purchase list")
            processPurchases(null)
        } else {
            processPurchases(result.purchasesList)
        }
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "onPurchasesUpdated: \$responseCode \$debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> if (purchases == null) {
                Log.d(TAG, "onPurchasesUpdated: null purchase list")
                processPurchases(null)
            } else {
                processPurchases(purchases)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Log.i(TAG, "onPurchasesUpdated: User canceled the purchase")
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> Log.i(TAG, "onPurchasesUpdated: The user already owns this item")
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> Log.e(TAG, "onPurchasesUpdated: Developer error means that Google Play " +
                    "does not recognize the configuration. If you are just getting started, " +
                    "make sure you have configured the application correctly in the " +
                    "Google Play Console. The SKU product ID must match and the APK you " +
                    "are using must be signed with release keys."
            )
        }
    }

    /**
     * Send purchase SingleLiveEvent and update purchases LiveData.
     *
     *
     * The SingleLiveEvent will trigger network call to verify the subscriptions on the sever.
     * The LiveData will allow Google Play settings UI to update based on the latest purchase data.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        if (purchasesList != null) {
            Log.d(TAG, "processPurchases: " + purchasesList.size + " purchase(s)")
        } else {
            Log.d(TAG, "processPurchases: with no purchases")
        }
        if (isUnchangedPurchaseList(purchasesList)) {
            Log.d(TAG, "processPurchases: Purchase list has not changed")
            return
        }
        purchaseUpdateEvent.postValue(purchasesList)
        purchases.postValue(purchasesList)
        purchasesList?.let { logAcknowledgementStatus(it) }
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        var ack_yes = 0
        var ack_no = 0
        for (purchase in purchasesList) {
            if (purchase.isAcknowledged) {
                ack_yes++
            } else {
                ack_no++
            }
        }
        Log.d(TAG, "logAcknowledgementStatus: acknowledged=" + ack_yes +
                " unacknowledged=" + ack_no)
    }

    /**
     * Check whether the purchases have changed before posting changes.
     */
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>?): Boolean {
        // TODO: Optimize to avoid updates with identical data.
        return false
    }

    /**
     * In order to make purchases, you need the [SkuDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [.onSkuDetailsResponse].
     */
    fun querySkuDetails() {
        Log.d(TAG, "querySkuDetails")
        val skus: MutableList<String> = ArrayList()
        skus.add(Constants.BASIC_SKU)
        skus.add(Constants.PREMIUM_SKU)
        val params = SkuDetailsParams.newBuilder()
                .setType(BillingClient.SkuType.SUBS)
                .setSkusList(skus)
                .build()
        Log.i(TAG, "querySkuDetailsAsync")
        billingClient!!.querySkuDetailsAsync(params, this)
    }

    /**
     * Launching the billing flow.
     *
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity?, params: BillingFlowParams): Int {
        val sku = params.sku
        val oldSku = params.oldSku
        Log.i(TAG, "launchBillingFlow: sku: $sku, oldSku: $oldSku")
        if (!billingClient!!.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient!!.launchBillingFlow(activity!!, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Acknowledge a purchase.
     *
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     *
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     *
     *
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     *
     *
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    fun acknowledgePurchase(purchaseToken: String?) {
        Log.d(TAG, "acknowledgePurchase")
        val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken!!)
                .build()
        billingClient!!.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            Log.d(TAG, "acknowledgePurchase: $responseCode $debugMessage")
        }
    }

    companion object {
        private const val TAG = "BillingLifecycle"

        @Volatile
        private var INSTANCE: BillingClientLifecycle? = null
        fun getInstance(app: Application): BillingClientLifecycle =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: BillingClientLifecycle(app).also { INSTANCE = it }
                }
    }
}