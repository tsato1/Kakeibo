package com.kakeibo.ui.search

import androidx.recyclerview.widget.RecyclerView

internal interface RecyclerItemTouchHelperListener {
    fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
}