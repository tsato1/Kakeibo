package com.kakeibo.feature_main.domain.use_cases.use_case_input

import android.content.Context
import com.kakeibo.R
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.DisplayedItem
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlin.jvm.Throws

class InsertItemUseCase(
    private val repository: DisplayedItemRepository,
    private val context: Context
) {

    @Throws(ItemEntity.InvalidItemException::class)
    suspend operator fun invoke(displayedItem: DisplayedItem): Long {
        val amountString = displayedItem.amount

        if ("" == amountString) {
            throw ItemEntity.InvalidItemException(
                context.getString(R.string.err_please_enter_amount)
            )
        }
        else if ("." == amountString || "," == amountString) {
            throw ItemEntity.InvalidItemException(
                context.getString(R.string.err_amount_invalid)
            )
        }

        val text2 = amountString.replace(',', '.')

        if (text2.toFloat() == 0f) {
            throw ItemEntity.InvalidItemException(
                context.getString(R.string.err_amount_cannot_be_0)
            )
        }

//        if (!UtilText.isAmountValid(text2)) {
//            throw ItemEntity.InvalidItemException("R.string.err_amount_invalid")
//        }

        return repository.insertItem(displayedItem)
    }

}