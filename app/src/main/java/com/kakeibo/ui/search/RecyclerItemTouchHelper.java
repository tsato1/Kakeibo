//package com.kakeibo.ui.search;
//
//import android.graphics.Canvas;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.ItemTouchHelper;
//
//import android.view.View;
//
//class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
//
//    private RecyclerItemTouchHelperListener _listener;
//
//    RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
//        super(dragDirs, swipeDirs);
//
//        _listener = listener;
//    }
//
//    @Override
//    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        return true;
//    }
//
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        if (_listener != null) {
//            _listener.onSwipe(viewHolder, direction, viewHolder.getAdapterPosition());
//        }
//    }
//
//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        View foregroundView = getForegroundView(viewHolder);
//        getDefaultUIUtil().clearView(foregroundView);
//    }
//
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        if (viewHolder != null) {
//            View foregroundView = getForegroundView(viewHolder);
//            getDefaultUIUtil().onSelected(foregroundView);
//        }
//    }
//
//    @Override
//    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//        return super.convertToAbsoluteDirection(flags, layoutDirection);
//    }
//
//    @Override
//    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        View foregroundView = getForegroundView(viewHolder);
//        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//    }
//
//    @Override
//    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        View foregroundView = getForegroundView(viewHolder);
//        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//    }
//
//    private View getForegroundView(RecyclerView.ViewHolder viewHolder) {
//        View foregroundView;
//
//        if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderDateRange) {
//            foregroundView = ((SearchRecyclerViewAdapter.ViewHolderDateRange) viewHolder).cardView;
//        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderAmountRange) {
//            foregroundView = ((SearchRecyclerViewAdapter.ViewHolderAmountRange) viewHolder).cardView;
//        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderCategory) {
//            foregroundView = ((SearchRecyclerViewAdapter.ViewHolderCategory) viewHolder).cardView;
//        } else if (viewHolder instanceof SearchRecyclerViewAdapter.ViewHolderMemo) {
//            foregroundView = ((SearchRecyclerViewAdapter.ViewHolderMemo) viewHolder).cardView;
//        } else {
//            foregroundView = null;
//        }
//
//        return foregroundView;
//    }
//}