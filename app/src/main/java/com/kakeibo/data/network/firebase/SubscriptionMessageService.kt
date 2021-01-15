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

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // todo needs to be implemented. originally this function onNewToken was not there
//        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
//        if(currentUser != null){
//            FirebaseFirestore.getInstance().collection("user").document(currentUser).update("deviceToken",token)
//        }
    }

    companion object {
        private const val TAG = "SubscriptionMsgService"
        private const val REMOTE_MESSAGE_SUBSCRIPTIONS_KEY = "currentStatus"
    }
}