package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.ItemStatus
import java.math.BigDecimal

class ItemStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<ItemStatus>> = repository.items

    private val itemsThisMonth: LiveData<List<ItemStatus>> = repository.itemsThisMonth
    private val itemsMutable = MutableLiveData<List<ItemStatus>>()
    val items = MediatorLiveData<List<ItemStatus>>()
    init {
        items.addSource(itemsMutable) { value -> items.value = value }
        items.addSource(itemsThisMonth) { value -> items.value = value }
    }
    fun setMutableAll(input: List<ItemStatus>) { // for showing search result or Report_C and D
        itemsMutable.value = input
    }
    fun setItemsThisMonth() { // to go back to default report (this month)
        items.value = itemsThisMonth.value
    }
    fun setItemsYM(y: String, m: String) {
        all.value?.let { list ->
            items.value = list.filter{
                it.eventDate.split("-")[0] == y && it.eventDate.split("-")[1] == m
            }
        }
    }

    private val itemsByCategory: LiveData<Map<Pair<Int, BigDecimal>, List<ItemStatus>>> =
            Transformations.map(items) { all ->
                all.groupBy { it.categoryCode }
                        .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
    }
    /* used in PieGraph in ReportC */
    val itemsIncome: LiveData<List<BigDecimal>> =
            Transformations.map(itemsByCategory) { allByCategory ->
                allByCategory.keys.map { it.second }
                        .filter { it > BigDecimal(0) }
                        .sortedDescending()
    }
    /* used in PieGraph in ReportC */
    val itemsExpense: LiveData<List<BigDecimal>> =
            Transformations.map(itemsByCategory) { allByCategory ->
                allByCategory.keys.map { it.second }
                        .filter { it < BigDecimal(0) }
                        .sortedDescending()
    }

    val income: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.filter { it.getAmount() > BigDecimal(0) }.sumOf { it.getAmount() }
    }
    val expense: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.filter { it.getAmount() < BigDecimal(0) }.sumOf { it.getAmount() }
    }
    val balance: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.sumOf { it.getAmount() }
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