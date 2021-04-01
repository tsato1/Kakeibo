package com.kakeibo.ui.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Medium : ViewModel() {

    companion object {
        const val FRAGMENT_INPUT = 0
        const val FRAGMENT_REPORT = 10
        const val FRAGMENT_REPORT_A = 11 // Report by Amount
        const val FRAGMENT_REPORT_B = 12 // Report by B
        const val FRAGMENT_REPORT_C = 13 // Report by Category
        const val FRAGMENT_REPORT_D = 14 // Report by Date
        const val FRAGMENT_SEARCH = 20
    }

    private var previouslyShown: Int = FRAGMENT_REPORT_D
    val currentlyShown = MutableLiveData(FRAGMENT_INPUT)
    fun setCurrentlyShown(value: Int) {
        if (value == FRAGMENT_REPORT) {
            currentlyShown.value = previouslyShown
        } else {
            if (currentlyShown.value == FRAGMENT_REPORT_A ||
                    currentlyShown.value == FRAGMENT_REPORT_B ||
                    currentlyShown.value == FRAGMENT_REPORT_C ||
                    currentlyShown.value == FRAGMENT_REPORT_D) {
                previouslyShown = currentlyShown.value!!
            }
            currentlyShown.value = value
        }
    }
    fun getCurrentlyShown(): Int {
        return currentlyShown.value!!
    }

    val inSearchResult = MutableLiveData(false)
    fun setInSearchResult(value: Boolean) {
        inSearchResult.value = value
    }
    fun getInSearchResult(): Boolean {
        return inSearchResult.value!!
    }
}