package com.kakeibo.ui.model

import com.kakeibo.core.data.local.entities.ItemEntity
import java.math.BigDecimal

class ExpandableListRowModel {

    companion object{
        const val PARENT = 1
        const val CHILD = 2
        /* const val BUTTONLAYOUT = 3
         const val REMOVELIST = 4
         const val EMPTYLAYOUT = 5*/
    }

    data class Header(val date: String, val income: BigDecimal, val expense: BigDecimal)

    lateinit var itemParent: Header
    lateinit var itemEntityChild : ItemEntity
    var type : Int
    var isExpanded : Boolean
    private var isCloseShown : Boolean

    constructor(type : Int,
                itemParent: Header,
                isExpanded : Boolean = false,
                isCloseShown : Boolean = false
    ){
        this.type = type
        this.itemParent = itemParent
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }

    constructor(
        type : Int,
        itemEntityChild : ItemEntity,
        isExpanded : Boolean = false,
        isCloseShown : Boolean = false
    ){
        this.type = type
        this.itemEntityChild = itemEntityChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}