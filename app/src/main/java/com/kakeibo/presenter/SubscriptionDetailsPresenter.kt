package com.kakeibo.presenter

import android.text.TextUtils
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.kakeibo.data.SubscriptionContent

/**
 * Presenter class used to bind and display metadata from a SubscriptionContent object
 */
class SubscriptionDetailsPresenter : AbstractDetailsDescriptionPresenter() {
    public override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val subscription = item as SubscriptionContent

        viewHolder.title.text = subscription.title
        viewHolder.subtitle.text = subscription.subtitle

        if (!TextUtils.isEmpty(subscription.description)) {
            viewHolder.body.text = subscription.description
        }
    }
}