package com.kakeibo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.Item
import com.kakeibo.util.UtilDate
import java.math.BigDecimal

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<Item>> = repository.items

    private val itemsThisYear: LiveData<List<Item>> = repository.itemsThisYear

    private val itemsThisMonth: LiveData<List<Item>> = repository.itemsThisMonth
//    private val itemsThisMonth: LiveData<List<Item>> = Transformations.map(itemsThisYear) { items ->
//        items.filter {
//            it.eventDate.contains(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
//        }
//    }
    private val itemsMutable = MutableLiveData<List<Item>>()
    val items = MediatorLiveData<List<Item>>()
    init {
        items.addSource(itemsMutable) { value -> items.value = value }
        items.addSource(itemsThisMonth) { value -> items.value = value }
    }
    fun setMutableAll(input: List<Item>) { // for showing search result
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

    private val itemsByCategory: LiveData<Map<Pair<Int, BigDecimal>, List<Item>>> =
            Transformations.map(items) { all ->
                all.groupBy { it.categoryCode }
                        .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.amount} ) }
    }
    /* used in PieGraph in ReportC */
    val itemsIncome: LiveData<List<Pair<Int, BigDecimal>>> = Transformations.map(itemsByCategory) { map ->
        map.keys.map { it }
                .filter { it.second > BigDecimal(0) }
                .map { Pair(it.first, it.second) }
                .sortedWith(compareBy({it.second}, {it.first}))
                .asReversed()
    }
    /* used in PieGraph in ReportC */
    val itemsExpense: LiveData<List<Pair<Int, BigDecimal>>> = Transformations.map(itemsByCategory) { map ->
        map.keys.map { it }
                .filter { it.second < BigDecimal(0) }
                .map { Pair(it.first, it.second.abs()) }
                .sortedWith(compareBy({it.second}, {it.first}))
                .asReversed()
    }

    val income: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.filter { it.amount > BigDecimal(0) }.sumOf { it.amount }
    }
    val expense: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.filter { it.amount < BigDecimal(0) }.sumOf { it.amount }
    }
    val balance: LiveData<BigDecimal> = Transformations.map(items) { items ->
        items.sumOf { it.amount }
    }

    fun insert(item: Item) {
        repository.insertItem(item)
    }

    fun deleteAll() {
        repository.deleteAllItems()
    }

    fun delete(id: Long) {
        repository.deleteItem(id)
    }

    fun isCategoryAlreadyUsed(code: Int): Boolean {
        all.value?.let { all ->
            all.forEach {
                if (it.categoryCode == code) return true }
        }
        return false
    }
}