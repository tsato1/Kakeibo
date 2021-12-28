package com.kakeibo.util

import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.ui.model.Query

object UtilQuery {
    fun query(all: List<DisplayedItemModel>, q: Query): List<DisplayedItemModel> {
        var out = all

        if (q.flagEventDate) {
            out = all.filter { it.eventDate in q.fromEventDate..q.toEventDate }
        }
        if (q.flagAmount) {
            out = out.filter { it.amount.toBigDecimal().abs() in q.fromAmount..q.toAmount }
        }
        if (q.flagCategory) {
            out = out.filter { it.categoryCode == q.categoryCode }
        }
        if (q.flagMemo) {
            out = out.filter { it.memo == q.memo }
        }
        if (q.flagUpdateDate) {
            out = out.filter { it.updateDate in q.fromUpdateDate..q.toUpdateDate }
        }

        return out
    }
}