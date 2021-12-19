package com.kakeibo.core.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.data.local.entities.KkbApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KkbAppViewModel @Inject constructor(
    private val dao: KkbAppDao
) : ViewModel() {

    val firstEntry: LiveData<KkbApp> = dao.getFirst()

    fun updateVal2(val2: Int) {
        viewModelScope.launch {
            dao.updateVal2(val2)
        }
    }
}