package com.kakeibo.util;

import android.content.res.Resources;

import com.kakeibo.data.SubscriptionStatus;
import com.kakeibo.R;
import com.kakeibo.billing.BillingUtilities;

public class UtilSubscription {

    /**
     * Return the resource string for the basic subscription button.
     *
     * Add an asterisk if the subscription is not local and might not be modifiable on this device.
     */
    public static String basicTextForSubscription(Resources res, SubscriptionStatus subscription) {
        String text;

        if (BillingUtilities.isAccountHold(subscription)) {
            text = res.getString(R.string.subscription_option_basic_message_account_hold);
        } else if (BillingUtilities.isGracePeriod(subscription)) {
            text = res.getString(R.string.subscription_option_basic_message_grace_period);
        } else if (BillingUtilities.isSubscriptionRestore(subscription)) {
            text = res.getString(R.string.subscription_option_basic_message_restore);
        } else if (BillingUtilities.isBasicContent(subscription)) {
            text = res.getString(R.string.subscription_option_basic_message_current);
        } else {
            text =  res.getString(R.string.subscription_option_basic_message);
        }
        if (subscription.isLocalPurchase) {
            return text;
        } else {
            // No local record, so the subscription cannot be managed on this device.
            return text + "*";
        }
    }

    /**
     * Return the resource string for the premium subscription button.
     *
     * Add an asterisk if the subscription is not local and might not be modifiable on this device.
     */
    public static String premiumTextForSubscription(Resources res,
                                                    SubscriptionStatus subscription) {
        String text;
        if (BillingUtilities.isAccountHold(subscription)) {
            text = res.getString(R.string.subscription_option_premium_message_account_hold);
        } else if (BillingUtilities.isGracePeriod(subscription)) {
            text = res.getString(R.string.subscription_option_premium_message_grace_period);
        } else if (BillingUtilities.isSubscriptionRestore(subscription)) {
            text =res.getString(R.string.subscription_option_premium_message_restore);
        } else if (BillingUtilities.isPremiumContent(subscription)) {
            text = res.getString(R.string.subscription_option_premium_message_current);
        } else {
            text = res.getString(R.string.subscription_option_premium_message);
        }

        if (subscription.isLocalPurchase) {
            return text;
        } else {
            // No local record, so the subscription cannot be managed on this device.
            return text + "*";
        }
    }
}