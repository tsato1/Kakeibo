package com.kakeibo.data.network.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakeibo.Constants
import com.kakeibo.billing.BillingUtilities.isBasicContent
import com.kakeibo.billing.BillingUtilities.isPremiumContent
import com.kakeibo.data.ContentResource
import com.kakeibo.data.Subscription
import java.util.*

/**
 * Fake implementation of ServerFunctions
 */
class FakeServerFunctions : ServerFunctions {
    /**
     * Live data is true when there are pending network requests.
     */
    override val loading = MutableLiveData<Boolean>()

    /**
     * The latest subscription data.
     *
     *
     * Use this class by observing the subscriptions [LiveData].
     * Fake data will be communicated through this LiveData.
     */
    override val subscriptions = MutableLiveData<List<Subscription>>()

    /**
     * The basic content URL.
     */
    override val basicContent = MutableLiveData<ContentResource>()

    /**
     * The premium content URL.
     */
    override val premiumContent = MutableLiveData<ContentResource>()

    private var fakeDataIndex = 0

    /**
     * Fetch fake basic content and post results to [.basicContent].
     * This will fail if the user does not have a basic subscription.
     */
    override fun updateBasicContent() {
        val subs = subscriptions.value
        if (subs == null || subs.isEmpty()) {
            basicContent.postValue(null)
            return
        }
        // Premium subscriptions also give access to basic content.
        if (isBasicContent(subs[0]) ||
                isPremiumContent(subs[0])) {
            basicContent.postValue(ContentResource("https://example.com/basic.jpg"))
        } else {
            basicContent.postValue(null)
        }
    }

    /**
     * Fetch fake premium content and post results to [.premiumContent].
     * This will fail if the user does not have a premium subscription.
     */
    override fun updatePremiumContent() {
        val subs = subscriptions.value
        if (subs == null || subs.isEmpty()) {
            premiumContent.postValue(null)
            return
        }
        if (isPremiumContent(subs[0])) {
            premiumContent.postValue(ContentResource("https://example.com/premium.jpg"))
        } else {
            premiumContent.postValue(null)
        }
    }

    /**
     * Fetches fake subscription data and posts successful results to [.subscriptions].
     */
    override fun updateSubscriptionStatus() {
        val nextSub: MutableList<Subscription> = ArrayList()
        val subscriptionStatus = nextFakeSubscription()
        if (subscriptionStatus != null) {
            nextSub.add(subscriptionStatus)
        }
        subscriptions.postValue(nextSub)
    }

    /**
     * Register a subscription with the server and posts successful results to
     * [.subscriptions].
     */
    override fun registerSubscription(sku: String, purchaseToken: String) {
        // When successful, return subscription results.
        // When response code is HTTP 409 CONFLICT create an already owned subscription.
        when (sku) {
            Constants.BASIC_SKU -> subscriptions.postValue(listOf(createFakeBasicSubscription()))
            Constants.PREMIUM_SKU -> subscriptions.postValue(listOf(createFakePremiumSubscription()))
            else -> subscriptions.postValue(listOf(
                    createAlreadyOwnedSubscription(sku, purchaseToken)))
        }
    }

    /**
     * Transfer subscription to this account posts successful results to [.subscriptions].
     */
    override fun transferSubscription(sku: String, purchaseToken: String) {
        val subscription = createFakeBasicSubscription()
        subscription.sku = sku
        subscription.purchaseToken = purchaseToken
        subscription.subAlreadyOwned = false
        subscription.isEntitlementActive = true
        subscriptions.postValue(listOf(subscription))
    }

    /**
     * Register Instance ID when the user signs in or the token is refreshed.
     */
    override fun registerInstanceId(instanceId: String) {}

    /**
     * Unregister when the user signs out.
     */
    override fun unregisterInstanceId(instanceId: String) {}

    /**
     * Create a local record of a subscription that is already owned by someone else.
     * Created when the server returns HTTP 409 CONFLICT after a subscription registration request.
     */
    private fun createAlreadyOwnedSubscription(
            sku: String,
            purchaseToken: String): Subscription {
        val subscriptionStatus = Subscription()
        subscriptionStatus.sku = sku
        subscriptionStatus.purchaseToken = purchaseToken
        subscriptionStatus.isEntitlementActive = false
        subscriptionStatus.subAlreadyOwned = true
        return subscriptionStatus
    }

    private fun nextFakeSubscription(): Subscription? {
        val subscription: Subscription?
        subscription = when (fakeDataIndex) {
            0 -> null
            1 -> createFakeBasicSubscription()
            2 -> createFakePremiumSubscription()
            3 -> createFakeAccountHoldSubscription()
            4 -> createFakeGracePeriodSubscription()
            5 -> createFakeAlreadyOwnedSubscription()
            6 -> createFakeCanceledBasicSubscription()
            7 -> createFakeCanceledPremiumSubscription()
            else ->                 // Unknown fake index, just pick one.
                null
        }
        // Iterate through fake data for testing purposes.
        fakeDataIndex = (fakeDataIndex + 1) % 8
        return subscription
    }

    private fun createFakeBasicSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = true
        subscription.willRenew = true
        subscription.sku = Constants.BASIC_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = false
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    private fun createFakePremiumSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = true
        subscription.willRenew = true
        subscription.sku = Constants.PREMIUM_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = false
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    private fun createFakeAccountHoldSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = false
        subscription.willRenew = true
        subscription.sku = Constants.PREMIUM_SKU
        subscription.isAccountHold = true
        subscription.isGracePeriod = false
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    private fun createFakeGracePeriodSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = true
        subscription.willRenew = true
        subscription.sku = Constants.BASIC_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = true
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    private fun createFakeAlreadyOwnedSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = false
        subscription.willRenew = true
        subscription.sku = Constants.BASIC_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = false
        subscription.purchaseToken = Constants.BASIC_SKU // Fake data!!
        subscription.subAlreadyOwned = true
        return subscription
    }

    private fun createFakeCanceledBasicSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = true
        subscription.willRenew = false
        subscription.sku = Constants.BASIC_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = false
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    private fun createFakeCanceledPremiumSubscription(): Subscription {
        val subscription = Subscription()
        subscription.isEntitlementActive = true
        subscription.willRenew = false
        subscription.sku = Constants.PREMIUM_SKU
        subscription.isAccountHold = false
        subscription.isGracePeriod = false
        subscription.purchaseToken = null
        subscription.subAlreadyOwned = false
        return subscription
    }

    companion object {

        @Volatile
        private var INSTANCE: FakeServerFunctions? = null

        fun getInstance(): ServerFunctions =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: FakeServerFunctions().also { INSTANCE = it }
                }
    }
}