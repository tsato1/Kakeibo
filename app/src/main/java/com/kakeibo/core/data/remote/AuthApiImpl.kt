package com.kakeibo.core.data.remote

import com.kakeibo.core.data.remote.requests.AuthRequest
import com.kakeibo.core.data.remote.requests.TokenRequest
import com.kakeibo.core.data.remote.responses.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {

    override suspend fun authenticate(accessToken: String) {
        client.get(AuthApi.EndPoints.authenticate.url) {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }

    override suspend fun register(authRequest: AuthRequest) {
        client.post(AuthApi.EndPoints.register.url) {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
    }

    override suspend fun login(authRequest: AuthRequest): AuthResponse {
        return client.post(AuthApi.EndPoints.login.url) {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }.body()
    }

    override suspend fun refreshAccessToken(
        refreshToken: String,
        tokenRequest: TokenRequest
    ): AuthResponse {
        return client.post(AuthApi.EndPoints.refreshAccessToken.url) {
            contentType(ContentType.Application.Json)
            setBody(tokenRequest)
            header(HttpHeaders.Authorization, "Bearer $refreshToken")
        }.body()
    }

    override suspend fun logout(refreshToken: String) {
        client.delete(AuthApi.EndPoints.logout.url) {
            header(HttpHeaders.Authorization, "Bearer $refreshToken")
        }
    }
}