//package com.kakeibo.core.data.repositories
//
//import com.kakeibo.core.data.local.KkbAppDao
//import com.kakeibo.core.data.local.entities.KkbAppEntity
//import com.kakeibo.core.domain.models.KkbAppModel
//import com.kakeibo.core.domain.repositories.KkbAppRepository
//
//class KkbAppRepositoryImpl(
//    private val dao: KkbAppDao
//) : KkbAppRepository {
//
//    override suspend fun getFirstEntry(): KkbAppModel? {
//        return dao.getFirst()?.toKkbAppModel()
//    }
//
//    override suspend fun insertEntry(kkbAppEntity: KkbAppEntity): Long {
//        return dao.insert(kkbAppEntity)
//    }
//
//}