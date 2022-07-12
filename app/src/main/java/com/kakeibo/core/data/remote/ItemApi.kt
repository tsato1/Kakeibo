package com.kakeibo.core.data.remote

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.remote.requests.DeleteItemRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ItemApi {

//    @POST("/register")
//    suspend fun register(@Body registerRequest: AccountRequest): Response<SimpleResponse>
//
//    @POST("/login")
//    suspend fun login(@Body loginRequest: AccountRequest): Response<SimpleResponse>

    @POST("/addItem")
    suspend fun addItem(@Body itemEntity: ItemEntity): Response<ResponseBody>

    @POST("/deleteItem")
    suspend fun deleteItem(@Body deleteNoteRequest: DeleteItemRequest): Response<ResponseBody>

//    @POST("/addOwnerToItem")
//    suspend fun addOwnerToItem(@Body addOwnerRequest: AddOwnerRequest): Response<SimpleResponse>

    @GET("/getItems")
    suspend fun getItems(): Response<List<ItemEntity>>

}