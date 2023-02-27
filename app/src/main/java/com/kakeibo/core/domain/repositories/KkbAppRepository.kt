package com.kakeibo.core.domain.repositories

//import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.core.domain.models.KkbAppModel

interface KkbAppRepository {

    suspend fun getFirstEntry(): KkbAppModel?

//    suspend fun insertEntry(kkbAppEntity: KkbAppEntity): Long

}