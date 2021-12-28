package com.kakeibo.core.presentation

import androidx.lifecycle.ViewModel
import com.kakeibo.core.AppPreferencesImpl
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var appPreferences: AppPreferencesImpl

}