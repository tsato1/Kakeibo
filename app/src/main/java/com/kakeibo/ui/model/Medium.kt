package com.kakeibo.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Medium : ViewModel() {

    companion object {
        const val ERROR = -1
        const val FRAGMENT_INPUT = 0
        const val FRAGMENT_REPORT = 10
        const val FRAGMENT_REPORT_CATEGORY_YEARLY = 11 // Report by Category - Yearly
        const val FRAGMENT_REPORT_DATE_YEARLY = 12 // Report by Date - Yearly
        const val FRAGMENT_REPORT_CATEGORY_MONTHLY = 13 // Report by Category - Monthly
        const val FRAGMENT_REPORT_DATE_MONTHLY = 14 // Report by Date - Monthly
        const val FRAGMENT_SEARCH = 20
    }

    private var previouslyShown: Int = FRAGMENT_REPORT_DATE_MONTHLY
    private val currentlyShown = MutableLiveData(FRAGMENT_INPUT)
    val currentlyShownLive: LiveData<Int> = currentlyShown

    fun setCurrentlyShown(value: Int) {
        if (value == FRAGMENT_REPORT) {
            currentlyShown.value = previouslyShown
        } else {
            if (currentlyShown.value == FRAGMENT_REPORT_CATEGORY_YEARLY ||
                    currentlyShown.value == FRAGMENT_REPORT_DATE_YEARLY ||
                    currentlyShown.value == FRAGMENT_REPORT_CATEGORY_MONTHLY ||
                    currentlyShown.value == FRAGMENT_REPORT_DATE_MONTHLY) {
                previouslyShown = currentlyShown.value!!
            }
            currentlyShown.postValue(value)
        }
    }

    private val inSearchResult = MutableLiveData(false)
    fun setInSearchResult(value: Boolean) {
        inSearchResult.value = value
    }
    fun getInSearchResult(): Boolean {
        return inSearchResult.value!!
    }
}