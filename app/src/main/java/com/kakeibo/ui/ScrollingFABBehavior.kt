package com.kakeibo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kakeibo.util.UtilUI.getToolbarHeight

class ScrollingFABBehavior(
        context: Context?,
        attrs: AttributeSet?
) : CoordinatorLayout.Behavior<FloatingActionButton?>(context, attrs) {

    private val toolbarHeight: Int = getToolbarHeight(context!!)

    override fun layoutDependsOn(
            parent: CoordinatorLayout,
            fab: FloatingActionButton,
            dependency: View
    ): Boolean {

        return dependency is AppBarLayout
    }

    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            fab: FloatingActionButton,
            dependency: View
    ): Boolean {

        if (dependency is AppBarLayout) {
            val lp = fab.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = fab.height + fabBottomMargin
            val ratio = dependency.getY() / toolbarHeight.toFloat()
            fab.translationY = -distanceToScroll * ratio
        }
        return true
    }

}