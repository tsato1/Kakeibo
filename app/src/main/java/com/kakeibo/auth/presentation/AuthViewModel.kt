package com.kakeibo.auth.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.auth.domain.repositories.AuthRepository
import com.kakeibo.util.isEmailValid
import com.kakeibo.util.isPasswordValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(AuthState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    fun onEvent(event: AuthUiEvent) {
        when(event) {
            is AuthUiEvent.LoginEmailChanged -> {
                state = state.copy(loginEmail = event.value)
            }
            is AuthUiEvent.LoginPasswordChanged -> {
                state = state.copy(loginPassword = event.value)
            }
            is AuthUiEvent.Login -> {
                login()
            }
            is AuthUiEvent.RegisterEmailChanged -> {
                state = state.copy(registerEmail = event.value)
            }
            is AuthUiEvent.RegisterPasswordChanged -> {
                state = state.copy(registerPassword = event.value)
            }
            is AuthUiEvent.Register -> {
                register()
            }
            is AuthUiEvent.Logout -> {
                logout()
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            if (!isEmailValid(state.registerEmail) || !isPasswordValid(state.registerPassword)) {
                resultChannel.send(AuthResult.InvalidEmailOrPassword())
                return@launch
            }
            state = state.copy(isLoading = true)
            val result = repository.register(
                email = state.registerEmail,
                password = state.registerPassword
            )
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }

    private fun login() {
        viewModelScope.launch {
            if (!isEmailValid(state.loginEmail) || !isPasswordValid(state.loginPassword)) {
                resultChannel.send(AuthResult.InvalidEmailOrPassword())
                Log.d("asdf", "login ${state.loginEmail} ${state.loginPassword}")
                return@launch
            }
            state = state.copy(isLoading = true)
            val result = repository.login(
                email = state.loginEmail,
                password = state.loginPassword
            )
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }

    private fun refreshAccessToken() {
        viewModelScope.launch {
            val result = repository.refreshAccessToken()
            resultChannel.send(result)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = repository.logout()
            resultChannel.send(result)
            state = state.copy(isLoading = false)
        }
    }

    sealed class AuthUiEvent {
        data class RegisterEmailChanged(val value: String): AuthUiEvent()
        data class RegisterPasswordChanged(val value: String): AuthUiEvent()
        object Register: AuthUiEvent()

        data class LoginEmailChanged(val value: String): AuthUiEvent()
        data class LoginPasswordChanged(val value: String): AuthUiEvent()
        object Login: AuthUiEvent()

        object Logout: AuthUiEvent()
    }

}