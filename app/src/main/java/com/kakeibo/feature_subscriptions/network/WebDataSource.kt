//package com.kakeibo.data.network
//
//import androidx.lifecycle.LiveData
//import com.kakeibo.AppExecutors
//import com.kakeibo.data.Subscription
//import com.kakeibo.data.network.firebase.ServerFunctions
//import java.util.concurrent.Executor
//
///**
// * Execute network requests on the network thread.
// * Fetch data from a [ServerFunctions] object and expose with [.getSubscriptions].
// */
//class WebDataSource private constructor(
//        private val executor: Executor,
//        private val serverFunctions: ServerFunctions) {
//    /**
//     * Live data is true when there are pending network requests.
//     */
//    val loading: LiveData<Boolean>
//        get() = serverFunctions.loading
//
//    /**
//     * LiveData with the [Subscription] information.
//     */
//    val subscriptions = serverFunctions.subscriptions
//
//    /**
//     * Live Data with the basic content.
//     */
//    val basicContent = serverFunctions.basicContent
//
//    /**
//     * Live Data with the premium content.
//     */
//    val premiumContent = serverFunctions.premiumContent
//
//    /**
//     * GET basic content.
//     */
//    fun updateBasicContent() = serverFunctions.updateBasicContent()
//
//    /**
//     * GET premium content.
//     */
//    fun updatePremiumContent() = serverFunctions.updatePremiumContent()
//
//    /**
//     * GET request for subscription status.
//     */
//    fun updateSubscription() {
//        executor.execute {
//            synchronized(WebDataSource::class.java) {
//                serverFunctions.updateSubscriptionStatus()
//            }
//        }
//    }
//
//    /**
//     * POST request to register subscription.
//     */
//    fun registerSubscription(sku: String, purchaseToken: String) {
//        executor.execute {
//            synchronized(WebDataSource::class.java) {
//                serverFunctions.registerSubscription(sku, purchaseToken)
//            }
//        }
//    }
//
//    /**
//     * POST request to transfer a subscription that is owned by someone else.
//     */
//    fun postTransferSubscriptionSync(sku: String, purchaseToken: String) {
//        executor.execute {
//            synchronized(WebDataSource::class.java) {
//                serverFunctions.transferSubscription(sku, purchaseToken)
//            }
//        }
//    }
//
//    /**
//     * POST request to register an Instance ID.
//     */
//    fun postRegisterInstanceId(instanceId: String) {
//        executor.execute { synchronized(WebDataSource::class.java) { serverFunctions.registerInstanceId(instanceId) } }
//    }
//
//    /**
//     * POST request to unregister an Instance ID.
//     */
//    fun postUnregisterInstanceId(instanceId: String) {
//        executor.execute { synchronized(WebDataSource::class.java) { serverFunctions.unregisterInstanceId(instanceId) } }
//    }
//
//    companion object {
//
//        @Volatile
//        private var INSTANCE: WebDataSource? = null
//
//        fun getInstance(executors: AppExecutors, callableFunctions: ServerFunctions): WebDataSource =
//                INSTANCE ?: synchronized(this) {
//                    INSTANCE ?: WebDataSource(
//                            executors.networkIO,
//                            callableFunctions
//                    ).also { INSTANCE = it }
//                }
//    }
//}