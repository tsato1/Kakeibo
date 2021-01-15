package com.kakeibo.ui

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.billing.BillingUtilities.isAccountHold
import com.kakeibo.billing.BillingUtilities.isBasicContent
import com.kakeibo.billing.BillingUtilities.isGracePeriod
import com.kakeibo.billing.BillingUtilities.isPremiumContent
import com.kakeibo.billing.BillingUtilities.isSubscriptionRestore
import com.kakeibo.billing.BillingUtilities.isTransferRequired
import com.kakeibo.data.ContentResource
import com.kakeibo.data.SubscriptionStatus
import com.kakeibo.util.UtilSubscription.basicTextForSubscription
import com.kakeibo.util.UtilSubscription.premiumTextForSubscription
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "BindingAdapter"

/**
 * Update a loading progress bar when the status changes.
 *
 *
 * When the network state changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:loadingProgressBar attribute.
 */
@BindingAdapter("loadingProgressBar")
fun loadingProgressBar(view: ProgressBar, loading: Boolean) {
    view.visibility = if (loading) View.VISIBLE else View.GONE
}

/**
 * Update basic content when the URL changes.
 *
 *
 * When the image URL content changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:updateBasicContent attribute.
 */
@BindingAdapter("updateBasicContent")
fun updateBasicContent(view: View, basicContent: ContentResource?) {
    val image = view.findViewById<ImageView>(R.id.home_basic_image)
    val textView = view.findViewById<TextView>(R.id.home_basic_text)
    val url = basicContent?.url
    if (url != null) {
        image.run {
            Log.d(TAG, "Loading image for basic content: $url")
            visibility = View.VISIBLE
            Glide.with(view.context)
                    .load(url)
                    .into(this)
        }
        textView.run {
            text = view.resources.getString(R.string.basic_content_text)
        }
    } else {
        image.run {
            visibility = View.GONE
        }
        textView.run {
            text = view.resources.getString(R.string.no_basic_content)
        }
    }
}

/**
 * Update premium content on the Premium fragment when the URL changes.
 *
 *
 * When the image URL content changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:updatePremiumContent attribute.
 */
@BindingAdapter("updatePremiumContent")
fun updatePremiumContent(view: View, premiumContent: ContentResource?) {
    val image = view.findViewById<ImageView>(R.id.premium_premium_image)
    val textView = view.findViewById<TextView>(R.id.premium_premium_text)
    val url = premiumContent?.url
    if (url != null) {
        image.run {
            Log.d(TAG, "Loading image for premium content: $url")
            visibility = View.VISIBLE
            Glide.with(context)
                    .load(url)
                    .into(this)
        }
        textView.run {
            text = view.resources.getString(R.string.premium_content_text)
        }
    } else {
        image.run {
            visibility = View.GONE
        }
        textView.run {
            text = resources.getString(R.string.no_premium_content)
        }
    }
}

/**
 * Update subscription views on the Home fragment when the subscription changes.
 *
 *
 * When the subscription changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:updateHomeViews attribute.
 */
@BindingAdapter("updateHomeViews")
fun updateHomeViews(view: View, subscriptions: List<SubscriptionStatus>?) {
    val restoreMsg = view.findViewById<TextView>(R.id.home_restore_message)
    val paywallMsg = view.findViewById<View>(R.id.home_paywall_message)
    val gracePeriodMsg = view.findViewById<View>(R.id.home_grace_period_message)
    val transferMsg = view.findViewById<View>(R.id.home_transfer_message)
    val accountHoldMsg = view.findViewById<View>(R.id.home_account_hold_message)
    val basicMsg = view.findViewById<View>(R.id.home_basic_message)

    // Set visibility assuming no subscription is available.
    // If a subscription is found that meets certain criteria,
    // then the visibility of the paywall will be changed to View.GONE.
    paywallMsg.visibility = View.VISIBLE

    // The remaining views start hidden. If a subscription is found that meets each criteria,
    // then the visibility will be changed to View.VISIBLE.
    restoreMsg.visibility = View.GONE
    gracePeriodMsg.visibility = View.GONE
    transferMsg.visibility = View.GONE
    accountHoldMsg.visibility = View.GONE
    basicMsg.visibility = View.GONE
    // Update based on subscription information.
    subscriptions?.let {
        for (subscription in subscriptions) {
            if (isSubscriptionRestore(subscription)) {
                Log.d(TAG, "restore VISIBLE")
//                view.home_restore_message.run {
//                    visibility = View.VISIBLE
//                    val expiryDate = getHumanReadableExpiryDate(subscription)
//                    text = view.resources.getString(R.string.restore_message_with_date, expiryDate)
//                }
//                view.home_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isGracePeriod(subscription)) {
                Log.d(TAG, "grace period VISIBLE")
//                view.home_grace_period_message.visibility = View.VISIBLE
//                view.home_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isTransferRequired(subscription) && subscription.sku == Constants.BASIC_SKU) {
                Log.d(TAG, "transfer VISIBLE")
//                view.home_transfer_message.visibility = View.VISIBLE
//                view.home_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isAccountHold(subscription)) {
                Log.d(TAG, "account hold VISIBLE")
//                view.home_account_hold_message.visibility = View.VISIBLE
//                view.home_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isBasicContent(subscription) || isPremiumContent(subscription)) {
                Log.d(TAG, "basic VISIBLE")
//                view.home_basic_message.visibility = View.VISIBLE
//                view.home_paywall_message.visibility = View.GONE // Paywall gone.
            }
        }
    }
}

/**
 * Update subscription views on the Premium fragment when the subscription changes.
 *
 *
 * When the subscription changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:updatePremiumViews attribute.
 */
@BindingAdapter("updatePremiumViews")
fun updatePremiumViews(view: View, subscriptions: List<SubscriptionStatus>?) {
    // Set visibility assuming no subscription is available.
    // If a subscription is found that meets certain criteria, then the visibility of the paywall
    // will be changed to View.GONE.
//    view.premium_paywall_message.visibility = View.VISIBLE
    // The remaining views start hidden. If a subscription is found that meets each criteria,
    // then the visibility will be changed to View.VISIBLE.
//    view.premium_restore_message.visibility = View.GONE
//    view.premium_grace_period_message.visibility = View.GONE
//    view.premium_transfer_message.visibility = View.GONE
//    view.premium_account_hold_message.visibility = View.GONE
//    view.premium_premium_content.visibility = View.GONE
//    view.premium_upgrade_message.visibility = View.GONE

    // The Upgrade button should appear if the user has a basic subscription, but does not
    // have a premium subscription. This variable keeps track of whether a premium subscription
    // has been found when looking throug the list of subscriptions.
    var hasPremium = false
    // Update based on subscription information.
    subscriptions?.let {
        for (subscription in subscriptions) {
            if (isSubscriptionRestore(subscription)) {
                Log.d(TAG, "restore VISIBLE")
//                view.premium_restore_message.run {
//                    visibility = View.VISIBLE
//                    val expiryDate = getHumanReadableExpiryDate(subscription)
//                    text = view.resources.getString(R.string.restore_message_with_date, expiryDate)
//                }
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isGracePeriod(subscription)) {
                Log.d(TAG, "grace period VISIBLE")
//                view.premium_grace_period_message.visibility = View.VISIBLE
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isTransferRequired(subscription) && subscription.sku == Constants.PREMIUM_SKU) {
                Log.d(TAG, "transfer VISIBLE")
//                view.premium_transfer_message.visibility = View.VISIBLE
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
            }
            if (isAccountHold(subscription)) {
                Log.d(TAG, "account hold VISIBLE")
//                view.premium_account_hold_message.visibility = View.VISIBLE
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
            }

            // The upgrade message must be shown if there is a basic subscription
            // and there are zero premium subscriptions. We need to keep track of the premium
            // subscriptions and hide the upgrade message if we find any.
            if (isPremiumContent(subscription)) {
                Log.d(TAG, "premium VISIBLE")
//                view.premium_premium_content.visibility = View.VISIBLE
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
                // Make sure we do not ask for an upgrade when user has premium subscription.
                hasPremium = true
//                view.premium_upgrade_message.visibility = View.GONE
            }
            if (isBasicContent(subscription) && !isPremiumContent(subscription) && !hasPremium) {
                Log.d(TAG, "basic VISIBLE")
                // Upgrade message will be hidden if a premium subscription is found later.
//                view.premium_upgrade_message.visibility = View.VISIBLE
//                view.premium_paywall_message.visibility = View.GONE // Paywall gone.
            }

        }
    }
}

/**
 * Update views on the Settings fragment when the subscription changes.
 *
 *
 * When the subscription changes, the binding adapter triggers this view in the layout XML.
 * See the layout XML files for the app:updateSettingsViews attribute.
 */
@BindingAdapter("updateSettingsViews")
fun updateSettingsViews(view: View, subscriptions: List<SubscriptionStatus>?) {
    // Set default button text: it might be overridden based on the subscription state.
//    view.subscription_option_premium_button.text =
//            view.resources.getString(R.string.subscription_option_premium_message)
//    view.subscription_option_basic_button.text =
//            view.resources.getString(R.string.subscription_option_basic_message)
//    view.settings_transfer_message.visibility = View.GONE
//     Update based on subscription information.
    var basicRequiresTransfer = false
    var premiumRequiresTransfer = false
    subscriptions?.let {
        for (subscription in it) {
            val sku = subscription.sku
            when (sku) {
                Constants.BASIC_SKU -> {
//                    view.subscription_option_basic_button.text =
//                            basicTextForSubscription(view.resources, subscription)
//                    if (isTransferRequired(subscription)) {
//                        basicRequiresTransfer = true
//                    }
                }
                Constants.PREMIUM_SKU -> {
//                    view.subscription_option_premium_button.text =
//                            premiumTextForSubscription(view.resources, subscription)
//                    if (isTransferRequired(subscription)) {
//                        premiumRequiresTransfer = true
//                    }
                }
            }
        }
    }
    val message = when {
        basicRequiresTransfer && premiumRequiresTransfer -> {
            val basicName = view.resources.getString(R.string.basic_button_text)
            val premiumName = view.resources.getString(R.string.premium_button_text)
            view.resources.getString(
                    R.string.transfer_message_with_two_skus, basicName, premiumName)
        }
        basicRequiresTransfer -> {
            val basicName = view.resources.getString(R.string.basic_button_text)
            view.resources.getString(R.string.transfer_message_with_sku, basicName)
        }
        premiumRequiresTransfer -> {
            val premiumName = view.resources.getString(R.string.premium_button_text)
            view.resources.getString(R.string.transfer_message_with_sku, premiumName)
        }
        else -> null
    }
    if (message != null) {
        Log.d(TAG, "transfer VISIBLE")
//        view.settings_transfer_message.visibility = View.VISIBLE
//        view.settings_transfer_message_text.text = message
    } else {
//        view.settings_transfer_message_text.text =
//                view.resources.getString(R.string.transfer_message)
    }
}

/**
 * Get a readable expiry date from a subscription.
 */
private fun getHumanReadableExpiryDate(subscription: SubscriptionStatus): String {
    val milliSeconds = subscription.activeUntilMillisec
    val formatter = SimpleDateFormat.getDateInstance()
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    if (milliSeconds == 0L) {
        Log.d(TAG, "Suspicious time: 0 milliseconds. JSON: $subscription")
    } else {
        Log.d(TAG, "Expiry time millis: " + subscription.activeUntilMillisec)
    }
    return formatter.format(calendar.time)
}
