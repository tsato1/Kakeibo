package com.kakeibo.data.network.firebase

import androidx.lifecycle.LiveData
import com.kakeibo.data.ContentResource
import com.kakeibo.data.SubscriptionStatus

/**
 * Interface to perform the Firebase Function calls and expose the results with
 * [.getSubscriptions].
 *
 * Use this class by observing the [.getSubscriptions] LiveData.
 * Any server updates will be communicated through this LiveData.
 */
interface ServerFunctions {
    /**
     * Live data is true when there are pending network requests.
     */
    val loading: LiveData<Boolean>

    /**
     * The latest subscription data from the server.
     *
     * Must be observed and active in order to receive updates from the server.
     */
    val subscriptions: LiveData<List<SubscriptionStatus>>

    /**
     * The basic content URL.
     */
    val basicContent: LiveData<ContentResource>

    /**
     * The premium content URL.
     */
    val premiumContent: LiveData<ContentResource>

    /**
     * Fetch basic content and post results to [.getBasicContent].
     * This will fail if the user does not have a basic subscription.
     */
    fun updateBasicContent()

    /**
     * Fetch premium content and post results to [.getPremiumContent].
     * This will fail if the user does not have a premium subscription.
     */
    fun updatePremiumContent()

    /**
     * Fetches subscription data from the server and posts successful results to
     * [.getSubscriptions].
     */
    fun updateSubscriptionStatus()

    /**
     * Register a subscription with the server and posts successful results to
     * [.getSubscriptions].
     */
    fun registerSubscription(sku: String, purchaseToken: String)

    /**
     * Transfer subscription to this account posts successful results to
     * [.getSubscriptions].
     */
    fun transferSubscription(sku: String, purchaseToken: String)

    /**
     * Register Instance ID when the user signs in or the token is refreshed.
     */
    fun registerInstanceId(instanceId: String)

    /**
     * Unregister when the user signs out.
     */
    fun unregisterInstanceId(instanceId: String)
}