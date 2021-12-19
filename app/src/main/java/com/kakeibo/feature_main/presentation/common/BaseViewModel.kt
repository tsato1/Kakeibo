package com.kakeibo.feature_main.presentation.common

import androidx.lifecycle.ViewModel
import com.kakeibo.core.AppPreferencesImpl
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var appPreferences: AppPreferencesImpl

}