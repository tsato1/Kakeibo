package com.kakeibo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.KkbAppStatus

class KkbAppViewModel(application: Application): AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<KkbAppStatus> = repository.kkbApp
    
    fun update(kkbAppStatus: KkbAppStatus) {
        repository.updateKkbApp(kkbAppStatus)
    }

    fun updateVal2(val2: Int) {
        repository.updateVal2(val2)
    }
}