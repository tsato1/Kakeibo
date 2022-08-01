package com.kakeibo.feature_main.domain.use_cases.use_case_search

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kakeibo.R
import com.kakeibo.core.data.local.entities.SearchEntity
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.repositories.SearchRepository
import com.kakeibo.feature_main.presentation.item_search.CATEGORY_INVALID
import kotlinx.datetime.toLocalDate

class InsertSearchUseCase(
    private val searchRepository: SearchRepository,
    private val context: Context
) {

    @Throws(SearchEntity.InvalidSearchException::class)
    suspend operator fun invoke(searchModel: SearchModel): Long {
        /* Date */
        if (searchModel.fromDate != null && searchModel.toDate != null) {
            if (searchModel.fromDate!!.toLocalDate() > searchModel.toDate!!.toLocalDate()) {
                throw SearchEntity.InvalidSearchException(
                    context.getString(R.string.err_from_date_older)
                )
            }
        }

        /* Amount */
        if (searchModel.fromAmount != null && searchModel.toAmount != null) {
            if (searchModel.fromAmount == "" || searchModel.toAmount == "") {
                throw SearchEntity.InvalidSearchException(
                    context.getString(R.string.err_please_enter_amount)
                )
            }

            if (searchModel.fromAmount == "") {
                throw SearchEntity.InvalidSearchException(
                    context.getString(R.string.err_please_enter_min_amount)
                )
            }

            if (searchModel.toAmount == "") {
                throw SearchEntity.InvalidSearchException(
                    context.getString(R.string.err_please_enter_max_amount)
                )
            }

            if (searchModel.fromAmount!!.toBigDecimal() > searchModel.toAmount!!.toBigDecimal()) {
                throw SearchEntity.InvalidSearchException(
                    context.getString(R.string.err_min_amount_greater)
                )
            }
        }

        /* Category */
        if (searchModel.categoryCode != null && searchModel.categoryCode == CATEGORY_INVALID) {
            throw SearchEntity.InvalidSearchException(
                context.getString(R.string.err_please_select_category)
            )
        }

        /* Memo */
        if (searchModel.memo != null && searchModel.memo == "") {
            throw SearchEntity.InvalidSearchException(
                context.getString(R.string.err_memo_empty)
            )
        }

        /* No result found */
        searchRepository.getCountOfSearchResult(
            SimpleSQLiteQuery(searchModel.toCountQuery(), searchModel.toArgs().toTypedArray())
        ).also { count ->
            if (count== 0) {
                throw SearchEntity.NoResultFoundException(
                    context.getString(R.string.msg_no_result_found)
                )
            }
        }

        return searchRepository.insertSearch(searchModel)
    }

}