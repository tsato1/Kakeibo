//package com.kakeibo.ui.settings.category.replace
//
//import android.content.Context
//import android.graphics.Canvas
//import android.os.Handler
//import android.os.Looper
//import android.util.TypedValue
//import android.view.ViewConfiguration
//import android.view.animation.AnimationUtils
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.RecyclerView
//import com.kakeibo.R
//import com.kakeibo.ui.adapter.view.RecyclerViewAdapter
//import java.util.*
//
//class ItemMoveCallback(context: Context, private val list: ArrayList<GridItem>) : ItemTouchHelper.Callback() {
//
//    enum class ItemActionState {
//        IDLE, LONG_TOUCH_OR_SOMETHING_ELSE, DRAG, SWIPE, HANDLED_LONG_TOUCH
//    }
//
//    private val touchSlop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
////    val touchSlop = ViewConfiguration.get(this@MainActivity).scaledTouchSlop
//    private val longTouchTimeout = ViewConfiguration.getLongPressTimeout() * 2
//    private var touchState: ItemActionState = ItemActionState.IDLE
//    private var lastViewHolderPosHandled: Int? = null
//    private val handler = Handler(Looper.getMainLooper())
//    private val longTouchRunnable = Runnable {
//        if (lastViewHolderPosHandled != null && touchState == ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE) {
////            Log.d("AppLog", "timer timed out to trigger long touch")
//            onItemLongTouch(lastViewHolderPosHandled!!)
//        }
//    }
//    private val animation = AnimationUtils.loadAnimation(context, R.anim.wobble)
//
//    private fun onItemLongTouch(pos: Int) {
////        Log.d("AppLog", "longTouchTimeout:$longTouchTimeout")
////        val item = list[pos] as GridItem.ChildItem
////        Toast.makeText(this@MainActivity, "long touch on :$pos ", Toast.LENGTH_SHORT).show()
////        AlertDialog.Builder(requireContext()).setTitle("long touch").setMessage("long touch on pos: $pos - item ${item.data.name}").show()
//        touchState = ItemActionState.HANDLED_LONG_TOUCH
//        lastViewHolderPosHandled = null
//        handler.removeCallbacks(longTouchRunnable)
//    }
//
//    override fun onChildDrawOver(
//            c: Canvas,
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder?,
//            dX: Float,
//            dY: Float,
//            actionState: Int,
//            isCurrentlyActive: Boolean) {
//        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
////        Log.d("AppLog", "onChildDrawOver $dX $dY pos:${viewHolder?.adapterPosition} actionState:$actionState isCurrentlyActive:$isCurrentlyActive")
//        if (touchState == ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE && (dX >= touchSlop || dY >= touchSlop)) {
//            lastViewHolderPosHandled = null
//            handler.removeCallbacks(longTouchRunnable)
//            touchState =
//                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) ItemActionState.DRAG
//                    else ItemActionState.SWIPE
////            Log.d("AppLog", "decided it's not a long touch, but $touchState instead")
//        }
//    }
//
//    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        super.onSelectedChanged(viewHolder, actionState)
////        Log.d("AppLog", "onSelectedChanged adapterPosition: ${viewHolder?.adapterPosition} actionState:$actionState")
//        when (actionState) {
//            ItemTouchHelper.ACTION_STATE_IDLE -> {
//                /* user finished drag or long touch */
//                if (touchState == ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE)
//                    onItemLongTouch(lastViewHolderPosHandled!!)
//                touchState = ItemActionState.IDLE
//                handler.removeCallbacks(longTouchRunnable)
//                lastViewHolderPosHandled = null
//            }
//            ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.ACTION_STATE_SWIPE -> {
//                if (touchState == ItemActionState.IDLE) {
////                    Log.d("AppLog", "setting timer to trigger long touch")
//
//                    lastViewHolderPosHandled = viewHolder!!.bindingAdapterPosition
//                    handler.removeCallbacks(longTouchRunnable)
//                    /* started as long touch, but could also be dragging or swiping ... */
//                    touchState = ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE
//                    handler.postDelayed(longTouchRunnable, longTouchTimeout.toLong())
//                    viewHolder.itemView.startAnimation(animation)
//                }
//            }
//        }
//    }
//
//    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
////        Log.d("AppLog", "onMove")
//
//        if (touchState == ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE) {
//            lastViewHolderPosHandled = null
//            handler.removeCallbacks(longTouchRunnable)
//            touchState = ItemActionState.DRAG
//        }
//        if (viewHolder.itemViewType != target.itemViewType)
//            return false
//        val fromPosition = viewHolder.bindingAdapterPosition
//        val toPosition = target.bindingAdapterPosition
//        //                if (fromPosition < toPosition)
//        //                    for (i in fromPosition until toPosition)
//        //                        Collections.swap(items, i, i + 1)
//        //                 else
//        //                    for (i in fromPosition downTo toPosition + 1)
//        //                        Collections.swap(items, i, i - 1)
//        val item = list.removeAt(fromPosition)
//        list.add(toPosition, item)
//        //                recyclerView.adapter!!.notifyItemRemoved(fromPosition)
//        //                recyclerView.adapter!!.notifyItemInserted(toPosition)
//        //                Collections.swap(items, fromPosition, toPosition)
//        //                recyclerView.adapter!!.notifyDataSetChanged()
//        recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
//        return true
//    }
//
//    override fun isLongPressDragEnabled(): Boolean = true
//
//    override fun isItemViewSwipeEnabled(): Boolean = false
//
//    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//        if (viewHolder.itemViewType == GridItem.ITEM_TYPE_PARENT)
//            return makeMovementFlags(0, 0)
////        Log.d("AppLog", "getMovementFlags")
//        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        val swipeFlags = if (isItemViewSwipeEnabled) ItemTouchHelper.START or ItemTouchHelper.END else 0
//        return makeMovementFlags(dragFlags, swipeFlags)
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
////                if (touchState == ItemActionState.LONG_TOUCH_OR_SOMETHING_ELSE) {
////                    lastViewHolderPosHandled = null
////                    handler.removeCallbacks(longTouchRunnable)
////                    touchState = ItemActionState.DRAG
////                }
////                val position = viewHolder.bindingAdapterPosition
////                items.removeAt(position)
////                recyclerView.adapter!!.notifyItemRemoved(position)
//    }
//
//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        super.clearView(recyclerView, viewHolder)
//        if (viewHolder is RecyclerViewAdapter.ViewHolder) {
//            val myViewHolder: RecyclerViewAdapter.ViewHolder = viewHolder
//            myViewHolder.itemView.clearAnimation()
//        }
//    }
//}