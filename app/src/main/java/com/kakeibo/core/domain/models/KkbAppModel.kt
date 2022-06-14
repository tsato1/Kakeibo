package com.kakeibo.core.domain.models

import com.kakeibo.core.data.local.entities.KkbAppEntity

data class KkbAppModel(
    val id: Long = 1,
    val name: String = "",
    val type: String = "",
    val updateDate: String = "",
    val intVal1: Int = 0,
    val intVal2: Int = -1,
    val intVal3: Int = 0,
    val strVal1: String = "",
    val strVal2: String = "",
    val strVal3: String = ""
) {
    fun toKkbAppEntity(): KkbAppEntity {
        return KkbAppEntity(
            id = id,
            name = name,
            type = type,
            updateDate = updateDate,
            valInt1 = intVal1,
            valInt2 = intVal2,
            valInt3 = intVal3,
            valStr1 = strVal1,
            valStr2 = strVal2,
            valStr3 = strVal3
        )
    }
}