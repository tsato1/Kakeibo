package com.kakeibo.settings.category.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomCategoryViewModel : ViewModel() {
    val id = MutableLiveData(-1L)
    fun setId(value: Long) { id.value = value }

    val code = MutableLiveData(-1)
    fun setCode(value: Int) { code.value = value }

    val localtion = MutableLiveData(-1) // for CategoryDsp
    fun setLocation(value: Int) { localtion.value = value }

    val name = MutableLiveData("")
    fun setName(value: String) { name.value = value }

    val color = MutableLiveData(-1)
    fun setColor(value: Int) { color.value = value }

    val significance = MutableLiveData(-1)
    fun setSignificance(value: Int) { significance.value = value }

    val image = MutableLiveData<ByteArray>()
    fun setImage(value: ByteArray) { image.value = value }

    val parent = MutableLiveData(-1)
    fun setParent(value: Int) { parent.value = value }

    val description = MutableLiveData("")
    fun setDescription(value: String) { description.value = value }

    fun reset() {
        id.value = -1L
        code.value = -1
        localtion.value= -1
        name.value = ""
        parent.value = -1
        description.value = ""
    }
}