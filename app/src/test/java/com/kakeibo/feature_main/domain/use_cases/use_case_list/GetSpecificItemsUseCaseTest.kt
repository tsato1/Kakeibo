package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.google.common.truth.Truth.assertThat
import com.kakeibo.feature_main.data.repositories.FakeRepository
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.Before
import org.junit.Test

class GetSpecificItemsUseCaseTest {

    private lateinit var getSpecificItemsUseCase: GetSpecificItemsUseCase
    private lateinit var fakeRepository: FakeRepository

    val item1 = DisplayedItemModel(
        id = 1,
        amount = "500000",
        currencyCode = "---",
        categoryCode = 0,
        memo = "in",
        eventDate = "2022-05-11",
        updateDate = "update date",
        categoryName = "INCOME",
        categoryColor = 1,
        categorySign = 0,
        categoryDrawable = "ic_category_income",
        categoryImage = null,
        categoryParent = -1,
        categoryDescription = "",
        categorySavedDate = "saved date"
    )
    val item2 = DisplayedItemModel(
        id = 2,
        amount = "100000",
        currencyCode = "---",
        categoryCode = 1,
        memo = "in",
        eventDate = "2022-05-15",
        updateDate = "update date",
        categoryName = "COMM",
        categoryColor = 0,
        categorySign = 0,
        categoryDrawable = "ic_category_come",
        categoryImage = null,
        categoryParent = -1,
        categoryDescription = "",
        categorySavedDate = "saved date"
    )
    val item3 = DisplayedItemModel(
        id = 3,
        amount = "1100000",
        currencyCode = "---",
        categoryCode = 2,
        memo = "in",
        eventDate = "2022-05-01",
        updateDate = "update date",
        categoryName = "MEAL",
        categoryColor = 0,
        categorySign = 0,
        categoryDrawable = "ic_category_meal",
        categoryImage = null,
        categoryParent = -1,
        categoryDescription = "",
        categorySavedDate = "saved date"
    )

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        getSpecificItemsUseCase = GetSpecificItemsUseCase(fakeRepository)

        val itemsToInsert = mutableListOf<DisplayedItemModel>()
        itemsToInsert.add(item1)
        itemsToInsert.add(item2)
        itemsToInsert.add(item3)
        itemsToInsert.shuffle()
        runBlocking {
            fakeRepository.insertItems(itemsToInsert)
        }
    }

    @Test
    fun `Order items by eventDate ascending, correct order`() = runBlocking {
        val today = LocalDate(2022, 5, 25)
        val remainingDays = UtilDate.getRemainingDays(today.toYMDString(UtilDate.DATE_FORMAT_DB))

        val calendarFromDate = LocalDate(
            today.year, today.monthNumber, 1
        ).minus(UtilDate.getFirstDayOfMonth(today.toYMDString(UtilDate.DATE_FORMAT_DB)), DateTimeUnit.DAY)
        val calendarToDate = LocalDate(
            today.year, today.monthNumber, 1
        ) + DatePeriod(months = 1) - DatePeriod(days = 1) + DatePeriod(days = remainingDays)

        val searchModel = SearchModel(
            fromDate = calendarFromDate.toYMDString(UtilDate.DATE_FORMAT_DB),
            toDate = calendarToDate.toYMDString(UtilDate.DATE_FORMAT_DB)
        )
        val items = getSpecificItemsUseCase(searchModel.toQuery(), searchModel.toArgs()).first()

        for (i in 0 .. items.data!!.size - 2) {
            assertThat(items.data!![i].eventDate).isAtMost(items.data!![i].eventDate)
        }
    }

    @Test
    fun `Search within an amount range, correct items`() = runBlocking {
        val fromAmount = "400000"
        val toAmount = "900000"

        val searchModel = SearchModel(
            fromAmount = fromAmount,
            toAmount = toAmount
        )
        val items = getSpecificItemsUseCase(searchModel.toQuery(), searchModel.toArgs()).first()

        assertThat(items.data!!).contains(item2)
    }
}