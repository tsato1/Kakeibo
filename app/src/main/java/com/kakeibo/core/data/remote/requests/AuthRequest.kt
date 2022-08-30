package com.kakeibo.core.data.remote.requests

data class AuthRequest(
    val email: String,
    val password: String,
    val device: Device
)