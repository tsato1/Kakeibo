package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.ItemStatus
import java.math.BigDecimal

class ItemStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<ItemStatus>> = repository.items
    val allByDate: LiveData<Map<Pair<String, BigDecimal>, List<ItemStatus>>> =
            Transformations.map(all) { all ->
                all.groupBy { it.eventDate }
                        .mapKeys { entry -> Pair(entry.key, entry.value.sumOf { it.getAmount() }) }
            }
    val allByCategory: LiveData<Map<Pair<Int, BigDecimal>, List<ItemStatus>>> =
            Transformations.map(all) { all ->
                all.groupBy { it.categoryCode }
                        .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            }
    val income: LiveData<BigDecimal> = Transformations.map(all) { all ->
        all.filter { it.getAmount() > BigDecimal(0) }.sumOf { it.getAmount() }
    }
    val expense: LiveData<BigDecimal> = Transformations.map(all) { all ->
        all.filter { it.getAmount() < BigDecimal(0) }.sumOf { it.getAmount() }
    }
    val balance: LiveData<BigDecimal> = Transformations.map(all) { all ->
        all.sumOf { it.getAmount() }
    }

    fun insert(itemStatus: ItemStatus) {
        repository.insertItem(itemStatus)
    }

    fun deleteAll() {
        repository.deleteAllItems()
    }

    fun delete(id: Long) {
        repository.deleteItem(id)
    }
}