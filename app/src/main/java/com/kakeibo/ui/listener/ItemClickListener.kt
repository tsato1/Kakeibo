package com.kakeibo.ui.listener

import android.view.View
import com.kakeibo.data.ItemStatus

interface ItemClickListener {
    fun onItemClicked(view: View, itemStatus: ItemStatus)
}