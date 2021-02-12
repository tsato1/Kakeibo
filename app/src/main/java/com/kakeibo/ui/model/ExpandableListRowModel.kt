package com.kakeibo.ui.model

import com.kakeibo.data.ItemStatus

class ExpandableListRowModel {

    companion object{
        const val PARENT = 1
        const val CHILD = 2
        /* const val BUTTONLAYOUT = 3
         const val REMOVELIST = 4
         const val EMPTYLAYOUT = 5*/
    }

    lateinit var itemParent: String
    lateinit var itemChild : ItemStatus
    var type : Int
    var isExpanded : Boolean
    private var isCloseShown : Boolean

    constructor(type : Int,
                itemParent: String,
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
            itemChild : ItemStatus,
            isExpanded : Boolean = false,
            isCloseShown : Boolean = false
    ){
        this.type = type
        this.itemChild = itemChild
        this.isExpanded = isExpanded
        this.isCloseShown = isCloseShown
    }
}