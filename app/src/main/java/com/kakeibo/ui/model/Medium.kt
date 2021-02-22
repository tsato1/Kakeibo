package com.kakeibo.ui.model

import androidx.databinding.ObservableInt

class Medium {

    companion object {
        const val FRAGMENT_INPUT = 0
        const val FRAGMENT_REPORT = 1
        const val FRAGMENT_SEARCH = 2

        const val REPORT_C = 10
        const val REPORT_D = 11
        const val REPORT_S = 20 // On REPORT tab, showing search result

        const val DIALOG = 100
    }

    val currentlyShown = ObservableInt()
}