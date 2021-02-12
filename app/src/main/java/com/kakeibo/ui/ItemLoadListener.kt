package com.kakeibo.ui

import com.kakeibo.ui.model.Balance

interface ItemLoadListener {
    fun onItemsLoaded(balance: Balance)
}