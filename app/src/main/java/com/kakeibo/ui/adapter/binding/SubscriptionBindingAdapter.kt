//package com.kakeibo.ui.adapter.binding
//
//import android.content.Context
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.TextView
//import androidx.databinding.BindingAdapter
//import androidx.databinding.DataBindingUtil
//import com.bumptech.glide.Glide
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.MobileAds
//import com.kakeibo.BuildConfig
//import com.kakeibo.Constants
//import com.kakeibo.R
//import com.kakeibo.billing.BillingUtilities.isAccountHold
//import com.kakeibo.billing.BillingUtilities.isBasicContent
//import com.kakeibo.billing.BillingUtilities.isGracePeriod
//import com.kakeibo.billing.BillingUtilities.isPremiumContent
//import com.kakeibo.billing.BillingUtilities.isSubscriptionRestore
//import com.kakeibo.billing.BillingUtilities.isTransferRequired
//import com.kakeibo.data.ContentResource
//import com.kakeibo.feature_settings.settings_category.domain.model.KkbApp
//import com.kakeibo.data.Subscription
//import com.kakeibo.databinding.ActivityInAppPurchaseBinding
//import java.text.SimpleDateFormat
//import java.util.*
//
//private const val TAG = "BindingAdapter"
//
///**
// * Update a loading progress bar when the status changes.
// *
// *
// * When the network state changes, the binding adapter triggers this view in the layout XML.
// * See the layout XML files for the app:loadingProgressBar attribute.
// */
//@BindingAdapter("loadingProgressBar")
//fun loadingProgressBar(view: ProgressBar, loading: Boolean) {
//    view.visibility = if (loading) View.VISIBLE else View.GONE
//}
//
///*
// * Update basic content when the URL changes.
// *
// *
// * When the image URL content changes, the binding adapter triggers this view in the layout XML.
// * See the layout XML files for the app:updateBasicContent attribute.
// */
//@BindingAdapter("updateBasicContent")
//fun updateBasicContent(view: View, basicContent: ContentResource?) {
//    val image = view.findViewById<ImageView>(R.id.basic_image)
//    val textView = view.findViewById<TextView>(R.id.basic_text)
//    val url = basicContent?.url
//    if (url != null) {
//        image.run {
//            Log.d(TAG, "Loading image for basic content: $url")
//            visibility = View.VISIBLE
//            Glide.with(view.context)
//                    .load(url)
//                    .into(this)
//        }
//        textView.run {
//            text = view.resources.getString(R.string.basic_content_text)
//        }
//    } else {
//        image.run {
//            visibility = View.GONE
//        }
//        textView.run {
//            text = view.resources.getString(R.string.no_basic_content)
//        }
//    }
//}
//
///*
// * Update premium content on the Premium fragment when the URL changes.
// *
// *
// * When the image URL content changes, the binding adapter triggers this view in the layout XML.
// * See the layout XML files for the app:updatePremiumContent attribute.
// */
//@BindingAdapter("updatePremiumContent")
//fun updatePremiumContent(view: View, premiumContent: ContentResource?) {
//    val image = view.findViewById<ImageView>(R.id.premium_premium_image)
//    val textView = view.findViewById<TextView>(R.id.premium_premium_text)
//    val url = premiumContent?.url
//    if (url != null) {
//        image.run {
//            Log.d(TAG, "Loading image for premium content: $url")
//            visibility = View.VISIBLE
//            Glide.with(context)
//                    .load(url)
//                    .into(this)
//        }
//        textView.run {
//            text = view.resources.getString(R.string.premium_content_text)
//        }
//    } else {
//        image.run {
//            visibility = View.GONE
//        }
//        textView.run {
//            text = resources.getString(R.string.no_premium_content)
//        }
//    }
//}
//
///*
// * Update subscription views on the Home fragment when the subscription changes.
// *
// *
// * When the subscription changes, the binding adapter triggers this view in the layout XML.
// * See the layout XML files for the app:updateHomeViews attribute.
// */
//@BindingAdapter("updateHomeViews")
//fun updateHomeViews(view: View, subscriptions: List<Subscription>?) {
//    // Set visibility assuming no subscription is available.
//    // If a subscription is found that meets certain criteria, then the visibility of the paywall
//    // will be changed to View.GONE.
//    val binding = DataBindingUtil.getBinding<ActivityInAppPurchaseBinding>(view)
//
//    binding?.basicPaywallMessage?.visibility = View.VISIBLE
//
//    // The remaining views start hidden. If a subscription is found that meets each criteria,
//    // then the visibility will be changed to View.VISIBLE.
//    binding?.restoreMessage?.visibility = View.GONE
//    binding?.gracePeriodMessage?.visibility = View.GONE
//    binding?.basicTransferMessage?.visibility = View.GONE
//    binding?.basicAccountHoldMessage?.visibility = View.GONE
//    binding?.basicMessage?.visibility = View.GONE
//    // Update based on subscription information.
//    subscriptions?.let {
//        for (subscription in subscriptions) {
//            if (isSubscriptionRestore(subscription)) {
//                Log.d(TAG, "restore VISIBLE")
//                binding?.restoreMessage?.run {
//                    visibility = View.VISIBLE
//                    val expiryDate = getHumanReadableExpiryDate(subscription)
//                    text = view.resources.getString(R.string.restore_message_with_date, expiryDate)
//                }
//                binding?.basicPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isGracePeriod(subscription)) {
//                Log.d(TAG, "grace period VISIBLE")
//                binding?.gracePeriodMessage?.visibility = View.VISIBLE
//                binding?.basicPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isTransferRequired(subscription) && subscription.sku == Constants.BASIC_SKU) {
//                Log.d(TAG, "transfer VISIBLE")
//                binding?.basicTransferMessage?.visibility = View.VISIBLE
//                binding?.basicPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isAccountHold(subscription)) {
//                Log.d(TAG, "account hold VISIBLE")
//                binding?.basicAccountHoldMessage?.visibility = View.VISIBLE
//                binding?.basicPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isBasicContent(subscription) || isPremiumContent(subscription)) {
//                Log.d(TAG, "basic VISIBLE")
//                binding?.basicMessage?.visibility = View.VISIBLE
//                binding?.basicPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//        }
//    }
//}
//
///*
// * Update subscription views on the Premium fragment when the subscription changes.
// *
// *
// * When the subscription changes, the binding adapter triggers this view in the layout XML.
// * See the layout XML files for the app:updatePremiumViews attribute.
// */
//@BindingAdapter("updatePremiumViews")
//fun updatePremiumViews(view: View, subscriptions: List<Subscription>?) {
//    val binding = DataBindingUtil.getBinding<ActivityInAppPurchaseBinding>(view)
//
//    // Set visibility assuming no subscription is available.
//    // If a subscription is found that meets certain criteria, then the visibility of the paywall
//    // will be changed to View.GONE.
//    binding?.let {
//        it.premiumPaywallMessage.visibility = View.VISIBLE
//        it.restoreMessage.visibility = View.GONE
//        it.gracePeriodMessage.visibility = View.GONE
//        it.premiumTransferMessage.visibility = View.GONE
//        it.premiumAccountHoldMessage.visibility = View.GONE
//        it.premiumPremiumContent.visibility = View.GONE
//        it.premiumUpgradeMessage.visibility = View.GONE
//    }
//
//    // The Upgrade button should appear if the user has a basic subscription, but does not
//    // have a premium subscription. This variable keeps track of whether a premium subscription
//    // has been found when looking throug the list of subscriptions.
//    var hasPremium = false
//    // Update based on subscription information.
//    subscriptions?.let {
//        for (subscription in subscriptions) {
//            if (isSubscriptionRestore(subscription)) {
//                Log.d(TAG, "restore VISIBLE")
//                binding?.restoreMessage?.run {
//                    visibility = View.VISIBLE
//                    val expiryDate = getHumanReadableExpiryDate(subscription)
//                    text = view.resources.getString(R.string.restore_message_with_date, expiryDate)
//                }
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isGracePeriod(subscription)) {
//                Log.d(TAG, "grace period VISIBLE")
//                binding?.gracePeriodMessage?.visibility = View.VISIBLE
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isTransferRequired(subscription) && subscription.sku == Constants.PREMIUM_SKU) {
//                Log.d(TAG, "transfer VISIBLE")
//                binding?.premiumTransferMessage?.visibility = View.VISIBLE
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//            if (isAccountHold(subscription)) {
//                Log.d(TAG, "account hold VISIBLE")
//                binding?.premiumAccountHoldMessage?.visibility = View.VISIBLE
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//
//            // The upgrade message must be shown if there is a basic subscription
//            // and there are zero premium subscriptions. We need to keep track of the premium
//            // subscriptions and hide the upgrade message if we find any.
//            if (isPremiumContent(subscription)) {
//                Log.d(TAG, "premium VISIBLE")
//                binding?.premiumPremiumContent?.visibility = View.VISIBLE
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//                // Make sure we do not ask for an upgrade when user has premium subscription.
//                hasPremium = true
//                binding?.premiumUpgradeMessage?.visibility = View.GONE
//            }
//            if (isBasicContent(subscription) && !isPremiumContent(subscription) && !hasPremium) {
//                Log.d(TAG, "basic VISIBLE")
//                // Upgrade message will be hidden if a premium subscription is found later.
//                binding?.premiumUpgradeMessage?.visibility = View.VISIBLE
//                binding?.premiumPaywallMessage?.visibility = View.GONE // Paywall gone.
//            }
//
//        }
//    }
//}
//
//@BindingAdapter("context", "kkbApp", "updateAdViews")
//fun updateAdViews(view: AdView, context: Context, kkbApp: KkbApp?, subscriptions: List<Subscription>?) {
//    Log.d("asdf","coming here")
//
//    kkbApp?.let { k ->
//        Log.d("asdf","make it visible! " + k.valInt2)
//        if (k.valInt2 == 0) { // val2 = -1:original, 0:agreed to show ads
//            MobileAds.initialize(context) { }
//
//            //Create an AdView and put it into your FrameLayout
//            val _adView = AdView(context)
//            if (BuildConfig.DEBUG) {
//                _adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
//                /* in debug mode */
//            } else {
//                /* view already has Id */
//            }
//
//            view.addView(_adView)
//
//            _adView.adSize = AdSize.BANNER
//            _adView.loadAd(AdRequest.Builder().build())
//
//
////            MobileAds.initialize(context) {}
////            val adRequest = AdRequest.Builder().build()
////            view.loadAd(adRequest)
//
////            view.visibility = View.VISIBLE
//
////            subscriptions?.let {
////                for (subscription in it) {
////                    if (subscription.sku == Constants.BASIC_SKU || subscription.sku == Constants.PREMIUM_SKU) {
////                        view.visibility = View.VISIBLE
////                    }
////                    else {
////                        view.visibility = View.GONE
////                    }
////                }
////            }
//        }
//        else {
////            view.visibility = View.GONE
//        }
//    }
//}
//
///*
// * Get a readable expiry date from a subscription.
// */
//private fun getHumanReadableExpiryDate(subscription: Subscription): String {
//    val milliSeconds = subscription.activeUntilMillisec
//    val formatter = SimpleDateFormat.getDateInstance()
//    val calendar = Calendar.getInstance()
//    calendar.timeInMillis = milliSeconds
//    if (milliSeconds == 0L) {
//        Log.d(TAG, "Suspicious time: 0 milliseconds. JSON: $subscription")
//    } else {
//        Log.d(TAG, "Expiry time millis: " + subscription.activeUntilMillisec)
//    }
//    return formatter.format(calendar.time)
//}
