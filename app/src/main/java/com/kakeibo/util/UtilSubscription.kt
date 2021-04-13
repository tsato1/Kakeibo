package com.kakeibo.util

import android.content.res.Resources
import com.kakeibo.R
import com.kakeibo.billing.BillingUtilities.isAccountHold
import com.kakeibo.billing.BillingUtilities.isBasicContent
import com.kakeibo.billing.BillingUtilities.isGracePeriod
import com.kakeibo.billing.BillingUtilities.isPremiumContent
import com.kakeibo.billing.BillingUtilities.isSubscriptionRestore
import com.kakeibo.data.Subscription

object UtilSubscription {
    /**
     * Return the resource string for the basic subscription button.
     *
     * Add an asterisk if the subscription is not local and might not be modifiable on this device.
     */
    fun basicTextForSubscription(res: Resources, subscription: Subscription): String {
        val text: String = if (isAccountHold(subscription)) {
            res.getString(R.string.subscription_option_basic_message_account_hold)
        } else if (isGracePeriod(subscription)) {
            res.getString(R.string.subscription_option_basic_message_grace_period)
        } else if (isSubscriptionRestore(subscription)) {
            res.getString(R.string.subscription_option_basic_message_restore)
        } else if (isBasicContent(subscription)) {
            res.getString(R.string.subscription_option_basic_message_current)
        } else {
            res.getString(R.string.subscription_option_basic_message)
        }
        return if (subscription.isLocalPurchase) {
            text
        } else {
            // No local record, so the subscription cannot be managed on this device.
            "$text*"
        }
    }

    /**
     * Return the resource string for the premium subscription button.
     *
     * Add an asterisk if the subscription is not local and might not be modifiable on this device.
     */
    fun premiumTextForSubscription(res: Resources,
                                   subscription: Subscription): String {
        val text: String = if (isAccountHold(subscription)) {
            res.getString(R.string.subscription_option_premium_message_account_hold)
        } else if (isGracePeriod(subscription)) {
            res.getString(R.string.subscription_option_premium_message_grace_period)
        } else if (isSubscriptionRestore(subscription)) {
            res.getString(R.string.subscription_option_premium_message_restore)
        } else if (isPremiumContent(subscription)) {
            res.getString(R.string.subscription_option_premium_message_current)
        } else {
            res.getString(R.string.subscription_option_premium_message)
        }
        return if (subscription.isLocalPurchase) {
            text
        } else {
            // No local record, so the subscription cannot be managed on this device.
            "$text*"
        }
    }
}