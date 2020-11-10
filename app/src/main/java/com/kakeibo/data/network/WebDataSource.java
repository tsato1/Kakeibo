package com.kakeibo.data.network;

import androidx.lifecycle.LiveData;

import com.kakeibo.AppExecutors;
import com.kakeibo.data.ContentResource;
import com.kakeibo.data.SubscriptionStatus;
import com.kakeibo.data.network.firebase.ServerFunctions;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Execute network requests on the network thread.
 * Fetch data from a {@link ServerFunctions} object and expose with {@link #getSubscriptions()}.
 */
public class WebDataSource {
    private static volatile WebDataSource INSTANCE = null;
    private Executor executor;
    private ServerFunctions serverFunctions;

    public static WebDataSource getInstance(AppExecutors executors,
                                            ServerFunctions callableFunctions) {
        if (INSTANCE == null) {
            synchronized (WebDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WebDataSource(executors.networkIO, callableFunctions);
                }
            }
        }
        return INSTANCE;
    }

    private WebDataSource(Executor executor, ServerFunctions serverFunctions) {
        this.executor = executor;
        this.serverFunctions = serverFunctions;
    }

    /**
     * Live data is true when there are pending network requests.
     */
    public LiveData<Boolean> getLoading() {
        return serverFunctions.getLoading();
    }

    public LiveData<List<SubscriptionStatus>> getSubscriptions() {
        return serverFunctions.getSubscriptions();
    }

    public LiveData<ContentResource> getBasicContent() {
        return serverFunctions.getBasicContent();
    }

    public LiveData<ContentResource> getPremiumContent() {
        return serverFunctions.getPremiumContent();
    }

    public void updateBasicContent() {
        serverFunctions.updateBasicContent();
    }

    public void updatePremiumContent() {
        serverFunctions.updatePremiumContent();
    }

    /**
     * GET request for subscription status.
     */
    public void updateSubscriptionStatus() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (WebDataSource.class) {
                    serverFunctions.updateSubscriptionStatus();
                }
            }
        });
    }

    /**
     * POST request to register subscription.
     */
    public void registerSubscription(final String sku, final String purchaseToken) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (WebDataSource.class) {
                    serverFunctions.registerSubscription(sku, purchaseToken);
                }
            }
        });
    }

    /**
     * POST request to transfer a subscription that is owned by someone else.
     */
    public void postTransferSubscriptionSync(final String sku, final String purchaseToken) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (WebDataSource.class) {
                    serverFunctions.transferSubscription(sku, purchaseToken);
                }
            }
        });
    }

    /**
     * POST request to register an Instance ID.
     */
    public void postRegisterInstanceId(final String instanceId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (WebDataSource.class) {
                    serverFunctions.registerInstanceId(instanceId);
                }
            }
        });
    }

    /**
     * POST request to unregister an Instance ID.
     */
    public void postUnregisterInstanceId(final String instanceId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (WebDataSource.class) {
                    serverFunctions.unregisterInstanceId(instanceId);
                }
            }
        });
    }
}
