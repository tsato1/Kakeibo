package com.kakeibo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class InstanceIDService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("NEW_TOKEN", s)
        sendRegistrationToServer(s)
    }

    /**
     * Persist token to servers.
     */
    private fun sendRegistrationToServer(token: String?) {
        if (token != null) {
            (application as SubApp).repository.registerInstanceId(token)
            // No need to unregister the previous Instance ID token because the server
            // automatically removes invalidated tokens.
        }
    }
}