package com.kakeibo.core.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.domain.repositories.AuthRepository
import com.kakeibo.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus: LiveData<Resource<String>> = _registerStatus

    fun login(email: String, password: String) {
        _loginStatus.postValue(Resource.Loading(null))

        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.postValue(Resource.Error("Please fill out all the field", null))
            return
        }

        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginStatus.postValue(result)
        }
    }

    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.postValue(Resource.Loading(null))

        if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            _registerStatus.postValue(Resource.Error("Please fill out all the field", null))
            return
        }

        if (password != repeatedPassword) {
            _registerStatus.postValue(Resource.Error("The passwords don't match", null))
            return
        }

        viewModelScope.launch {
            val result = repository.register(email, password)
            _registerStatus.postValue(result)
        }
    }

}