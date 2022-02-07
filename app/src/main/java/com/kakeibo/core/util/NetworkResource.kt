//package com.kakeibo.core.util
//
//
//data class NetworkResource<out T>(val status: Status, val data: T?, val message: String?) {
//
//    companion object {
//
//        fun <T> success(data: T?): NetworkResource<T> {
//            return NetworkResource(Status.SUCCESS, data, null)
//        }
//
//        fun <T> error(msg: String, data: T?): NetworkResource<T> {
//            return NetworkResource(Status.ERROR, data, msg)
//        }
//
//        fun <T> loading(data: T?): NetworkResource<T> {
//            return NetworkResource(Status.LOADING, data, null)
//        }
//
//    }
//
//}
//
//enum class Status {
//    SUCCESS,
//    ERROR,
//    LOADING
//}
