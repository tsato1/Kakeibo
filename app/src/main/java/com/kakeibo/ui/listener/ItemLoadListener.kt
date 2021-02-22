package com.kakeibo.ui.listener

import com.kakeibo.ui.model.Balance

interface ItemLoadListener {
    fun onItemsLoaded(balance: Balance)
}