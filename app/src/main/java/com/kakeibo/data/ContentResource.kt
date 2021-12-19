//package com.kakeibo.data
//
//import com.google.gson.Gson
//import com.google.gson.JsonSyntaxException
//
//class ContentResource(val url: String?) {
//    companion object {
//
//        private const val URL_KEY = "url"
//
//        fun listFromMap(map: Map<String, Any>): ContentResource? {
//            val url = map[URL_KEY]
//            return if (url is String) {
//                ContentResource(url as String?)
//            } else {
//                null
//            }
//        }
//
//        fun fromJsonString(dataString: String?): ContentResource? {
//            val gson = Gson()
//            return try {
//                gson.fromJson(dataString, ContentResource::class.java)
//            } catch (e: JsonSyntaxException) {
//                null
//            }
//        }
//    }
//}