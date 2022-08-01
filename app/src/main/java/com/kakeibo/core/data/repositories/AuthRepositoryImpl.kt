package com.kakeibo.core.data.repositories

import com.kakeibo.core.data.remote.AuthApi
import com.kakeibo.core.data.remote.requests.AccountRequest
import com.kakeibo.core.domain.repositories.AuthRepository
import com.kakeibo.core.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.login(AccountRequest(email, password))

            if (response.isSuccessful && response.body()!!.successful) {
                Resource.Success(response.body()?.message)
            }
            else {
                Resource.Error(response.body()?.message ?: response.message(), null)
            }
        }
        catch (e: Exception) {
            Resource.Error("Cannot connect to the server. Check your internet connection", null)
        }
    }

    override suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.register(AccountRequest(email, password))

            if (response.isSuccessful && response.body()!!.successful) {
                Resource.Success(response.body()?.message)
            }
            else {
                Resource.Error(response.body()?.message ?: response.message(), null)
            }
        }
        catch (e: Exception) {
            Resource.Error("Cannot connect to the server. Check your internet connection", null)
        }
    }
}