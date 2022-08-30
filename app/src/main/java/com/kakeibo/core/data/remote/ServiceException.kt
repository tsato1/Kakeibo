package com.kakeibo.core.data.remote

class ServiceException(private val statusCode: Int) : RuntimeException() {

    fun code(): Int {
        return statusCode
    }

}