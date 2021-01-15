package com.kakeibo.ui.search

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.ui.search.SearchRecyclerViewAdapter.*

internal class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val _listener: RecyclerItemTouchHelperListener?) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        _listener?.onSwipe(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = getForegroundView(viewHolder)
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = getForegroundView(viewHolder)
            getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = getForegroundView(viewHolder)
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundView = getForegroundView(viewHolder)
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive)
    }

    private fun getForegroundView(viewHolder: RecyclerView.ViewHolder): View? {
        val foregroundView: View?
        foregroundView = if (viewHolder is ViewHolderDateRange) {
            viewHolder.cardView
        } else if (viewHolder is ViewHolderAmountRange) {
            viewHolder.cardView
        } else if (viewHolder is ViewHolderCategory) {
            viewHolder.cardView
        } else if (viewHolder is ViewHolderMemo) {
            viewHolder.cardView
        } else {
            null
        }
        return foregroundView
    }
}