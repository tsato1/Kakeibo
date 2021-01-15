//package com.kakeibo.ui;
//
//import android.app.Application;
//import android.util.Log;
//
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.kakeibo.SubApp;
//import com.kakeibo.data.ContentResource;
//import com.kakeibo.data.DataRepository;
//import com.kakeibo.data.SubscriptionStatus;
//
//import java.util.List;
//
//public class SubscriptionStatusViewModel extends AndroidViewModel {
//
//    private static final String TAG = "SubViewModel";
//
//    /**
//     * Data repository.
//     */
//    private DataRepository repository;
//
//    /**
//     * True when there are pending network requests
//     */
//    public LiveData<Boolean> loading;
//
//    /**
//     * Subscriptions LiveData
//     */
//    public LiveData<List<SubscriptionStatus>> subscriptions;
//
//    public LiveData<ContentResource> basicContent;
//
//    public LiveData<ContentResource> premiumContent;
//
//    /**
//     * Keep track of the last Instance ID to be registered, so that it
//     * can be unregistered when the user signs out.
//     */
//    private String instanceIdToken = null;
//
//    public SubscriptionStatusViewModel(Application application) {
//        super(application);
//        repository = ((SubApp) application).getRepository();
//        loading = repository.getLoading();
//        subscriptions = repository.getSubscriptions();
//        basicContent = repository.getBasicContent();
//        premiumContent = repository.getPremiumContent();
//    }
//
//    public void unregisterInstanceId() {
//        // Unregister current Instance ID before the user signs out.
//        // This is an authenticated call, so you cannot do this after the sign-out has completed.
//        if (instanceIdToken != null) {
//            repository.unregisterInstanceId(instanceIdToken);
//        }
//    }
//
//    public void userChanged() {
//        repository.deleteLocalUserData();
//        String token = FirebaseInstanceId.getInstance().getToken();
//        if (token != null) {
//            registerInstanceId(token);
//        }
//        repository.fetchSubscriptions();
//    }
//
//    public void manualRefresh() {
//        repository.fetchSubscriptions();
//    }
//
//    private void registerInstanceId(String token) {
//        repository.registerInstanceId(token);
//        // Keep track of the Instance ID so that it can be unregistered.
//        instanceIdToken = token;
//    }
//
//    /**
//     * Register a new subscription.
//     */
//    public void registerSubscription(String sku, String purchaseToken) {
//        repository.registerSubscription(sku, purchaseToken);
//    }
//
//    /**
//     * Transfer the subscription to this account.
//     */
//    public void transferSubscriptions() {
//        Log.d(TAG, "transferSubscriptions");
//        List<SubscriptionStatus> subs = subscriptions.getValue();
//        if (subs != null) {
//            for (SubscriptionStatus subscription : subs) {
//                String sku = subscription.sku;
//                String purchaseToken = subscription.purchaseToken;
//                if (sku != null && purchaseToken != null) {
//                    repository.transferSubscription(sku, purchaseToken);
//                }
//            }
//        }
//    }
//}
//
