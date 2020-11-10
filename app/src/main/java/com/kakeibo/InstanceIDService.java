package com.kakeibo;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;

public class InstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }

    //deprecated , with FirebaseInstanceService
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        sendRegistrationToServer(refreshedToken);
//    }

    /**
     * Persist token to servers.
     */
    private void sendRegistrationToServer(@Nullable String token) {
        if (token != null) {
            ((SubApp) getApplication()).getRepository().registerInstanceId(token);
            // No need to unregister the previous Instance ID token because the server
            // automatically removes invalidated tokens.
        }
    }
}
