package com.kakeibo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.ItemStatus

class ItemStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val _repository: DataRepository
    val all: LiveData<List<ItemStatus>>

    fun insert(itemStatus: ItemStatus?) {
        _repository.insertItemStatus(itemStatus)
    }

    init {
        val subApp = application as SubApp
        _repository = subApp.repository
        all = _repository.items
    }
}