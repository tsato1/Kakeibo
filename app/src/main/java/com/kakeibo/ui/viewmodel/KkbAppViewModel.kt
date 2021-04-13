package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.DataRepository
import com.kakeibo.data.KkbApp

class KkbAppViewModel(application: Application): AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<KkbApp> = repository.kkbApp
    
    fun update(kkbApp: KkbApp) {
        repository.updateKkbApp(kkbApp)
    }

    fun updateVal2(val2: Int) {
        repository.updateVal2(val2)
    }
}