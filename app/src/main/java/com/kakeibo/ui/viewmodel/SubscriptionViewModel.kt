package com.kakeibo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.messaging.FirebaseMessaging
import com.kakeibo.SubApp
import com.kakeibo.data.Subscription

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Data repository.
     */
    private val repository = (application as SubApp).repository

    /**
     * True when there are pending network requests
     */
    val loading: LiveData<Boolean>
        get() = repository.loading

    /**
     * Subscriptions LiveData
     */
    val subscriptions: LiveData<List<Subscription>> = repository.subscriptions
    val basicContent = repository.basicContent
    val premiumContent = repository.premiumContent

    /**
     * Keep track of the last Instance ID to be registered, so that it
     * can be unregistered when the user signs out.
     */
    private var instanceIdToken: String? = null

    fun unregisterInstanceId() {
        // Unregister current Instance ID before the user signs out.
        // This is an authenticated call, so you cannot do this after the sign-out has completed.
        instanceIdToken?.let {
            repository.unregisterInstanceId(it)
        }
    }

    fun userChanged() {
        repository.deleteLocalUserData()
        FirebaseMessaging.getInstance().token.let {
            if(it.isComplete) {
                registerInstanceId(it.result.toString())
            }
        }
        repository.fetchSubscriptions()
    }

    fun manualRefresh() {
        repository.fetchSubscriptions()
    }

    private fun registerInstanceId(token: String) {
        repository.registerInstanceId(token)
        // Keep track of the Instance ID so that it can be unregistered.
        instanceIdToken = token
    }

    /**
     * Register a new subscription.
     */
    fun registerSubscription(sku: String, purchaseToken: String)  =
        repository.registerSubscription(sku, purchaseToken)

    /**
     * Transfer the subscription to this account.
     */
    fun transferSubscriptions() {
        Log.d(TAG, "transferSubscriptions")
        subscriptions.value?.let {
            for (subscription in it) {
                val sku = subscription.sku
                val purchaseToken = subscription.purchaseToken
                if (sku != null && purchaseToken != null) {
                    repository.transferSubscription(sku = sku, purchaseToken = purchaseToken)
                }
            }
        }
    }

    companion object {
        private const val TAG = "SubViewModel"
    }
}