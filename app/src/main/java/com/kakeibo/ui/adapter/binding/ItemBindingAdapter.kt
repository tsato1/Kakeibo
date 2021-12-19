//package com.kakeibo.ui.adapter
//
//import android.content.Context
//import android.text.SpannableString
//import android.text.TextUtils
//import android.widget.TextView
//import androidx.databinding.BindingAdapter
//import com.kakeibo.R
//import com.kakeibo.feature_item.presentation.MainActivity
//import com.kakeibo.util.UtilCurrency
//import com.kakeibo.util.UtilDate
//
//@BindingAdapter("bind:context", "bind:amount", "bind:colon")
//fun setAmount(textView: TextView, context: Context, amount: String?, colon: Boolean) {
//    amount?.let {
//        val text =
//                if (colon) { TextUtils.concat(SpannableString(context.getString(R.string.amount_colon)), UtilCurrency.getSignedAmount(it)) }
//                else UtilCurrency.getSignedAmount(it)
//        textView.text = text
//    }
//}
//
//@BindingAdapter("bind:context", "bind:eventDate", "bind:colon")
//fun setEventDate(textView: TextView, context: Context, eventDate: String?, colon: Boolean) {
////    eventDate?.let {
////        val text =
////                if (colon) context.getString(R.string.event_date_colon) +
////                        UtilDate.getDateWithDayFromDBDate(
////                                eventDate, MainActivity.weekNames, MainActivity.dateFormat)
////                else UtilDate.getDateWithDayFromDBDate(
////                        eventDate, MainActivity.weekNames, MainActivity.dateFormat)
////        textView.text = text
////    }
//}
//
//@BindingAdapter("bind:context", "bind:memo", "bind:colon")
//fun setMemo(textView: TextView, context: Context, memo: String?, colon: Boolean) {
//    memo?.let {
//        val text =
//                if (colon) context.getString(R.string.memo_colon) + memo
//                else memo
//        textView.text = text
//    }
//}
//
//
//@BindingAdapter("bind:context", "bind:updateDate", "bind:colon")
//fun setUpdateDate(textView: TextView, context: Context, updateDate: String?, colon: Boolean) {
//    updateDate?.let {
//        val text =
//                if (colon) context.getString(R.string.updated_on_colon) + updateDate
//                else updateDate
//        textView.text = text
//    }
//}
