package com.kakeibo.core.domain.repositories

import com.kakeibo.core.util.Resource

interface AuthRepository {

    suspend fun login(email: String, password: String): Resource<String>

    suspend fun register(email: String, password: String): Resource<String>

}