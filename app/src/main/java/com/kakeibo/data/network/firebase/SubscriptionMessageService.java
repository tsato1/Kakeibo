package com.kakeibo.data.network.firebase;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kakeibo.SubApp;
import com.kakeibo.data.SubscriptionStatus;

import java.util.List;
import java.util.Map;

public class SubscriptionMessageService extends FirebaseMessagingService {

    private static final String TAG = "SubscriptionMsgService";
    private static final String REMOTE_MESSAGE_SUBSCRIPTIONS_KEY = "currentStatus";

    @Override
    public void onMessageReceived(@Nullable RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            Log.i(TAG, "Received null remote message");
            return;
        }
        Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            List<SubscriptionStatus> result = null;
            if (data.containsKey(REMOTE_MESSAGE_SUBSCRIPTIONS_KEY)) {
                result = SubscriptionStatus
                        .listFromJsonString(data.get(REMOTE_MESSAGE_SUBSCRIPTIONS_KEY));
            }
            if (result == null) {
                Log.e(TAG, "Invalid subscription data");
            } else {
                ((SubApp) getApplication()).getRepository().updateSubscriptionsFromNetwork(result);
            }
        }
    }
}
