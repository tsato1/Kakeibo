package com.kakeibo.presenter;

import android.text.TextUtils;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.kakeibo.data.SubscriptionContent;

/**
 * Presenter class used to bind and display metadata from a SubscriptionContent object
 */
public class SubscriptionDetailsPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    public void onBindDescription(ViewHolder viewHolder, Object item) {
        SubscriptionContent subscription = (SubscriptionContent) item;

        if (subscription != null) {
            viewHolder.getTitle().setText(subscription.title);
            viewHolder.getSubtitle().setText(subscription.subtitle);
            if (!TextUtils.isEmpty(subscription.description)) {
                viewHolder.getBody().setText(subscription.description);
            }
        }
    }
}
