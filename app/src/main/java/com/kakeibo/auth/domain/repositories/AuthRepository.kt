package com.kakeibo.auth.domain.repositories

import com.kakeibo.auth.presentation.AuthResult
import com.kakeibo.core.data.remote.requests.TokenRequest

interface AuthRepository {

    suspend fun register(email: String, password: String): AuthResult<Unit>

    suspend fun login(email: String, password: String): AuthResult<Unit>

    suspend fun refreshAccessToken(): AuthResult<Unit>

    suspend fun logout(): AuthResult<Unit>

}