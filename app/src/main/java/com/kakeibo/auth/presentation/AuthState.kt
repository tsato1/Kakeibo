package com.kakeibo.auth.presentation

data class AuthState(
    val isLoading: Boolean = false,
    val registerEmail: String = "",
    val registerPassword: String = "",
    val loginEmail: String = "",
    val loginPassword: String = ""
)