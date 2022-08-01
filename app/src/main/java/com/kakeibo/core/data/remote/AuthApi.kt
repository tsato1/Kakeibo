package com.kakeibo.core.data.remote

import com.kakeibo.core.data.remote.requests.AccountRequest
import com.kakeibo.core.data.remote.responses.SimpleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/register")
    suspend fun register(@Body registerRequest: AccountRequest): Response<SimpleResponse>

    @POST("/login")
    suspend fun login(@Body loginRequest: AccountRequest): Response<SimpleResponse>

}