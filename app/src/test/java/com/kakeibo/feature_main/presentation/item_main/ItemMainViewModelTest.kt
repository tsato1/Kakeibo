package com.kakeibo.feature_main.presentation.item_main

import com.kakeibo.feature_main.data.repositories.FakeRepository
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.util.UtilCurrency
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ItemMainViewModelTest {

    private lateinit var itemMainViewModel: ItemMainViewModel

    @Before
    fun setUp() {
//        itemMainViewModel = ItemMainViewModel(
//            FakeRepository()
//        )
    }

    @Test
    fun `delete an item with id = 1L`() {
        itemMainViewModel.onEvent(
            ItemMainEvent.DeleteItem(
                DisplayedItemModel(
                    id = 1L,
                    amount = "",
                    currencyCode = UtilCurrency.CURRENCY_NONE,
                    categoryCode = 0,
                    memo = "",
                    eventDate = "",
                    updateDate = ""
                )
            )
        )
        /*
        not done yet. check the db if the item is successfully deleted and check if recentlyDeletedItem holds correct value
         */
    }
}