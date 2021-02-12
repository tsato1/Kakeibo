package com.kakeibo.data.network.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kakeibo.SubApp
import com.kakeibo.data.SubscriptionStatus

class SubscriptionMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            var result: List<SubscriptionStatus>? = null
            if (REMOTE_MESSAGE_SUBSCRIPTIONS_KEY in data) {
                result = data[REMOTE_MESSAGE_SUBSCRIPTIONS_KEY]?.let {
                    SubscriptionStatus.listFromJsonString(it)
                }
            }
            if (result == null) {
                Log.e(TAG, "Invalid subscription data")
            } else {
                val app = application as SubApp
                app.repository.updateSubscriptionsFromNetwork(result)
            }
        }
    }

    companion object {
        private const val TAG = "SubscriptionMsgService"
        private const val REMOTE_MESSAGE_SUBSCRIPTIONS_KEY = "currentStatus"
    }
}