package com.kakeibo.core.data.remote

import com.kakeibo.Constants
import com.kakeibo.core.data.remote.requests.AuthRequest
import com.kakeibo.core.data.remote.requests.TokenRequest
import com.kakeibo.core.data.remote.responses.AuthResponse

interface AuthApi {

    suspend fun register(authRequest: AuthRequest)

    suspend fun login(authRequest: AuthRequest): AuthResponse

    suspend fun refreshAccessToken(refreshToken: String, tokenRequest: TokenRequest): AuthResponse

    suspend fun logout(refreshToken: String, tokenRequest: TokenRequest)

    sealed class EndPoints(val url: String) {
        object register: EndPoints(Constants.AUTH_BASE_URL+"/register")
        object login: EndPoints(Constants.AUTH_BASE_URL+"/login")
        object refreshAccessToken: EndPoints(Constants.AUTH_BASE_URL+"/token")
        object logout: EndPoints(Constants.AUTH_BASE_URL+"/logout")
    }

}