package com.kakeibo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.ItemStatus

class ItemStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<ItemStatus>> = repository.items
    val itemsByMonth: LiveData<Map<String, List<ItemStatus>>> = Transformations.map(all) { all ->
        all.groupBy ({ it.eventDate }, { it })
    }

    fun insert(itemStatus: ItemStatus) {
        repository.insertItem(itemStatus)
    }

    fun getItemsByMonth(year: String, month: String) {
        repository.getItemsByMonth(year, month)
    }

    fun calcDaySum(): String {

        return "asdf"
    }

    fun deleteAll() {
        repository.deleteAllItems()
    }
}