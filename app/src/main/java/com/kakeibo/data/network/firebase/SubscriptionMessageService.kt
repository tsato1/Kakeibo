package com.kakeibo.data.network.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kakeibo.SubApp
import com.kakeibo.data.Subscription

class SubscriptionMessageService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SubscriptionMsgService"
        private const val REMOTE_MESSAGE_SUBSCRIPTIONS_KEY = "currentStatus"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived called")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            var result: List<Subscription>? = null
            if (REMOTE_MESSAGE_SUBSCRIPTIONS_KEY in data) {
                result = data[REMOTE_MESSAGE_SUBSCRIPTIONS_KEY]?.let {
                    Subscription.listFromJsonString(it)
                }
            }
            if (result == null) {
                Log.e(TAG, "Invalid subscription data")
            } else {
                val app = application as SubApp
                app.repository.updateSubscriptionsFromNetwork(result)
                Log.d(TAG, "storing data to repo")
            }
        } else {
            Log.d(TAG, "data is empty")
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
    }
}