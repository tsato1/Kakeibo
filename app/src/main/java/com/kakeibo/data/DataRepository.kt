package com.kakeibo.data

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.android.billingclient.api.Purchase
import com.kakeibo.Constants
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.data.disk.LocalDataSource
import com.kakeibo.data.network.WebDataSource
import java.util.*

class DataRepository private constructor(
        private val localDataSource: LocalDataSource,
        private val webDataSource: WebDataSource,
        private val billingClientLifecycle: BillingClientLifecycle) {

    /**
     * [MediatorLiveData] to coordinate updates from the database and the network.
     *
     *
     * The mediator observes multiple sources. The database source is immediately exposed.
     * The network source is stored in the database, which will eventually be exposed.
     * The mediator provides an easy way for us to use LiveData for both the local data source
     * and the network data source, without implementing a new callback interface.
     */
    val subscriptions = MediatorLiveData<List<SubscriptionStatus>>()

    /**
     * KkbApp
     */
    val kkbApp: LiveData<KkbAppStatus>

    /**
     * ItemStatus
     */
    val items: LiveData<List<ItemStatus>>
    val itemsThisYear: LiveData<List<ItemStatus>>
    val itemsThisMonth: LiveData<List<ItemStatus>>

    /**
     * CategoryStatus
     */
    val categories: LiveData<List<CategoryStatus>>
    val categoriesDisplayed: LiveData<List<CategoryStatus>>
    val categoriesNotDisplayed: LiveData<List<CategoryStatus>>

    /**
     * CategoryDspStatus
     */
    val categoryDspStatuses: LiveData<List<CategoryDspStatus>>

    /**
     * Live data with basic content
     */
    val basicContent = MediatorLiveData<ContentResource?>()

    /**
     * Live data with premium content
     */
    val premiumContent = MediatorLiveData<ContentResource?>()

    val loading: LiveData<Boolean>
        get() = webDataSource.loading

    fun updateSubscriptionsFromNetwork(remoteSubscriptions: List<SubscriptionStatus>?) {

        val oldSubscriptions = subscriptions.value!!
        val purchases = billingClientLifecycle.purchases.value
        val subscriptions =
                mergeSubscriptionsAndPurchases(oldSubscriptions, remoteSubscriptions, purchases)

        remoteSubscriptions?.let { acknowledgeRegisteredPurchaseTokens(it) }

        // Store the subscription information when it changes.
        localDataSource.updateSubscriptions(subscriptions)

        // Update the content when the subscription changes.
        if (remoteSubscriptions != null) {
            // Figure out which content we need to fetch.
            var updateBasic = false
            var updatePremium = false
            for (subscription in remoteSubscriptions) {
                if (Constants.BASIC_SKU == subscription.sku) {
                    updateBasic = true
                } else {
                    // Premium subscribers get access to basic content as well.
                    updateBasic = true
                    updatePremium = true
                }
            }

            if (updateBasic) {
                // Fetch the basic content.
                webDataSource.updateBasicContent()
            } else {
                // If we no longer own this content, clear it from the UI.
                basicContent.postValue(null)
            }
            if (updatePremium) {
                // Fetch the premium content.
                webDataSource.updatePremiumContent()
            } else {
                // If we no longer own this content, clear it from the UI.
                premiumContent.postValue(null)
            }
        }
    }

    /**
     * Acknowledge subscriptions that have been registered by the server.
     */
    private fun acknowledgeRegisteredPurchaseTokens(remoteSubscriptions: List<SubscriptionStatus>) {
        for (remoteSubscription in remoteSubscriptions) {
            val purchaseTkn = remoteSubscription.purchaseToken
            billingClientLifecycle.acknowledgePurchase(purchaseTkn)
        }
    }

    /**
     * TODO(122273956) - Simplify / improve merge algorithm
     * Merge the previous subscriptions and new subscriptions by looking at on-device purchases.
     *
     *
     * We want to return the list of new subscriptions, possibly with some modifications
     * based on old subscriptions and the on-devices purchases from Google Play Billing.
     * Old subscriptions should be retained if they are owned by someone else (subAlreadyOwned)
     * and the purchase token for the subscription is still on this device.
     */
    private fun mergeSubscriptionsAndPurchases(
            oldSubscriptions: List<SubscriptionStatus>?,
            newSubscriptions: List<SubscriptionStatus>?,
            purchases: List<Purchase>?
    ): List<SubscriptionStatus> {

        val subscriptionStatuses: MutableList<SubscriptionStatus> = ArrayList()

        purchases?.let { updateLocalPurchaseTokens(newSubscriptions, it) }
        newSubscriptions?.let { subscriptionStatuses.addAll(newSubscriptions) }

        // Find old subscriptions that are in purchases but not in new subscriptions.
        if (purchases != null && oldSubscriptions != null) {
            for (oldSubscription in oldSubscriptions) {
                if (oldSubscription.subAlreadyOwned && oldSubscription.isLocalPurchase) {
                    // This old subscription was previously marked as "already owned" by
                    // another user. It should be included in the output if the SKU
                    // and purchase token match their previous value.
                    for (purchase in purchases) {
                        if (purchase.sku == oldSubscription.sku
                                && purchase.purchaseToken == oldSubscription.purchaseToken) {
                            // The old subscription that was already owned subscription should
                            // be added to the new subscriptions.
                            // Look through the new subscriptions to see if it is there.
                            var foundNewSubscription = false
                            if (newSubscriptions != null) {
                                for (newSubscription in newSubscriptions) {
                                    if (TextUtils.equals(newSubscription.sku, oldSubscription.sku)) {
                                        foundNewSubscription = true
                                    }
                                }
                            }
                            if (!foundNewSubscription) {
                                // The old subscription should be added to the output.
                                // It matches a local purchase.
                                subscriptionStatuses.add(oldSubscription)
                            }
                        }
                    }
                }
            }
        }
        return subscriptionStatuses
    }

    /**
     * Modify the subscriptions isLocalPurchase field based on the list of local purchases.
     * Return true if any of the values changed.
     */
    private fun updateLocalPurchaseTokens(
            subscriptions: List<SubscriptionStatus>?,
            purchases: List<Purchase>?
    ): Boolean {

        var hasChanged = false

        if (subscriptions != null) {
            for (subscription in subscriptions) {
                var isLocalPurchase = false
                var purchaseToken = subscription.purchaseToken

                if (purchases != null) {
                    for (purchase in purchases) {
                        if (TextUtils.equals(subscription.sku, purchase.sku)) {
                            isLocalPurchase = true
                            purchaseToken = purchase.purchaseToken
                        }
                    }
                }

                if (subscription.isLocalPurchase != isLocalPurchase) {
                    subscription.isLocalPurchase = isLocalPurchase
                    subscription.purchaseToken = purchaseToken
                    hasChanged = true
                }
            }
        }
        return hasChanged
    }

    /**
     * Fetch subscriptions from the server and update local data source.
     */
    fun fetchSubscriptions() {
        webDataSource.updateSubscriptionStatus()
    }

    /**
     * Register subscription to this account and update local data source.
     */
    fun registerSubscription(sku: String?, purchaseToken: String?) {
        webDataSource.registerSubscription(sku!!, purchaseToken!!)
    }

    /**
     * Transfer subscription to this account and update local data source.
     */
    fun transferSubscription(sku: String?, purchaseToken: String?) {
        webDataSource.postTransferSubscriptionSync(sku!!, purchaseToken!!)
    }

    /**
     * Register Instance ID.
     */
    fun registerInstanceId(instanceId: String?) {
        webDataSource.postRegisterInstanceId(instanceId!!)
    }

    /**
     * Unregister Instance ID.
     */
    fun unregisterInstanceId(instanceId: String?) {
        webDataSource.postUnregisterInstanceId(instanceId!!)
    }

    /**
     * Delete local user data when the user signs out.
     */
    fun deleteLocalUserData() {
        localDataSource.deleteLocalUserData()
        basicContent.postValue(null)
        premiumContent.postValue(null)
    }

    /**
     *
     * Items, Categories, etc
     *
     */
    fun updateKkbApp(kkbAppStatus: KkbAppStatus) {
        localDataSource.updateKkbApp(kkbAppStatus)
    }

    fun updateVal2(val2: Int) {
        localDataSource.updateVal2(val2)
    }

    fun insertItem(itemStatus: ItemStatus) {
        localDataSource.insertItem(itemStatus)
    }

    fun getItemsByMonth(year: String, month: String) {
        localDataSource.getItemsByMonth(year, month)
    }

    fun insertCategory(categoryStatus: CategoryStatus) {
        localDataSource.insertCategory(categoryStatus)
    }

    fun insertCategoryDsps(categoryDspStatuses: List<CategoryDspStatus>) {
        localDataSource.insertCategoryDsps(categoryDspStatuses)
    }

    fun deleteAllItems() {
        localDataSource.deleteAllItems()
    }

    fun deleteItem(id: Long) {
        localDataSource.deleteItem(id)
    }

    fun deleteAllCategories() {
        localDataSource.deleteAllCategories()
    }

    fun deleteAllCategoryDsps() {
        localDataSource.deleteAllCategoryDsps()
    }

    fun deleteCategory(id: Long) {
        localDataSource.deleteCategory(id)
    }

    companion object {
        @Volatile
        private var INSTANCE: DataRepository? = null

        fun getInstance(localDataSource: LocalDataSource,
                        webDataSource: WebDataSource,
                        billingClientLifecycle: BillingClientLifecycle
        ): DataRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?:
                    DataRepository(localDataSource, webDataSource, billingClientLifecycle)
                            .also { INSTANCE = it }
                }
    }

    init {
        // Update content from the web.
        // We are using a MediatorLiveData so that we can clear the data immediately
        // when the subscription changes.
        basicContent.addSource(webDataSource.basicContent) {
            basicContent.postValue(it)
        }

        premiumContent.addSource(webDataSource.premiumContent) {
            premiumContent.postValue(it)
        }

        // Database changes are observed by the ViewModel.
        subscriptions.addSource(localDataSource.subscriptions) {
            val numOfSubscriptions = it?.size ?: 0
            Log.d("Repository", "Subscriptions updated: $numOfSubscriptions")
            subscriptions.postValue(it)
        }

        // Observed network changes are store in the database.
        // The database changes will propagate to the ViewModel.
        // We could write different logic to ensure that the network call completes when
        // the UI component is inactive.
        subscriptions.addSource(webDataSource.subscriptions) {
            updateSubscriptionsFromNetwork(it)
        }

        // When the list of purchases changes, we need to update the subscription status
        // to indicate whether the subscription is local or not. It is local if the
        // the Google Play Billing APIs return a Purchase record for the SKU. It is not
        // local if there is no record of the subscription on the device.
        subscriptions.addSource(billingClientLifecycle.purchases) {
            val subscriptionStatuses = subscriptions.value
            if (subscriptionStatuses != null) {
                val hasChanged = updateLocalPurchaseTokens(subscriptionStatuses, it)
                if (hasChanged) {
                    localDataSource.updateSubscriptions(subscriptionStatuses)
                }
            }
        }

        // Database changes are observed by the ViewModel.
        kkbApp = localDataSource.kkbApp
        items = localDataSource.items
        itemsThisYear = localDataSource.itemsThisYear
        itemsThisMonth = localDataSource.itemsThisMonth

        categories = localDataSource.categories
        categoriesDisplayed = localDataSource.categoriesDisplayed
        categoriesNotDisplayed = localDataSource.categoriesNotDisplayed
        categoryDspStatuses = localDataSource.categoryDsps
    }
}