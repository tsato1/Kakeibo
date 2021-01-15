package com.kakeibo.ui

import com.kakeibo.ui.items.Balance

interface ItemLoadListener {
    fun onItemsLoaded(balance: Balance)
}