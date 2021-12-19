//package com.kakeibo.ui.settings.category.edit
//
//import android.util.Log
//import androidx.lifecycle.*
//import com.kakeibo.core.data.local.CategoryDao
//import com.kakeibo.core.data.local.entities.CategoryEntity
//import com.kakeibo.util.UtilCategory.CUSTOM_CATEGORY_CODE_START
//import com.kakeibo.util.UtilCategory.NUM_MAX_CUSTOM_CATEGORY
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class CustomCategoryViewModel @Inject constructor(
//    private val dao: CategoryDao
//) : ViewModel() {
//
//    private val allCustomCategories: LiveData<List<CategoryEntity>> = dao.getAllCustomCategories()
//    val allMap: LiveData<Map<Int, CategoryEntity>> = Transformations.map(allCustomCategories) { all ->
//        all.associateBy( { it.code }, { it })
//    }
//
//    private var lastCode: Int? = -1
//
//    init {
//        getLastCode()
//    }
//
//    fun getLastCode() {
//        viewModelScope.launch {
//            lastCode = dao.getCustomCategoryLastCode() ?: -1
//        }
//    }
//
//    val dsp: LiveData<List<CategoryEntity>> = dao.getCategoriesDisplayed()
//    val nonDsp: LiveData<List<CategoryEntity>> = dao.getCategoriesNotDisplayed()
//    val custom: LiveData<List<CategoryEntity>> = Transformations.map(allCustomCategories) { all ->
//        all.filter { it.code >= CUSTOM_CATEGORY_CODE_START }
//    }
//
//    fun insert(category: CategoryEntity) {
//        viewModelScope.launch {
//            dao.insertCategory(category)
//        }
//        getLastCode()
//    }
//
//    fun delete(id: Long) {
//        viewModelScope.launch {
//            dao.deleteCategory(id)
//        }
//    }
//
//    /* for custom category */
//    fun getCodeForNewCustomCategory(): Int {
//        return if (lastCode == null || lastCode == -1) CUSTOM_CATEGORY_CODE_START else lastCode!! + 1
//    }
//
//    fun canCreateNewCustomCategory(): Int {
//        val newCode = getCodeForNewCustomCategory()
//        Log.d("asdf", "newCode=$newCode")
//        if (newCode >= CUSTOM_CATEGORY_CODE_START + NUM_MAX_CUSTOM_CATEGORY) {
//            return -2 // failure because the number of custom categories has reached max
//        }
//        return 1 // success
//    }
//
//
//
//    val id = MutableLiveData(-1L)
//    fun setId(value: Long) { id.value = value }
//
//    val code = MutableLiveData(-1)
//    fun setCode(value: Int) { code.value = value }
//
//    val localtion = MutableLiveData(-1) // for CategoryDsp
//    fun setLocation(value: Int) { localtion.value = value }
//
//    val name = MutableLiveData("")
//    fun setName(value: String) { name.value = value }
//
//    val color = MutableLiveData(-1)
//    fun setColor(value: Int) { color.value = value }
//
//    val significance = MutableLiveData(-1)
//    fun setSignificance(value: Int) { significance.value = value }
//
//    val image = MutableLiveData<ByteArray>()
//    fun setImage(value: ByteArray) { image.value = value }
//
//    val parent = MutableLiveData(-1)
//    fun setParent(value: Int) { parent.value = value }
//
//    val description = MutableLiveData("")
//    fun setDescription(value: String) { description.value = value }
//
//    fun reset() {
//        id.value = -1L
//        code.value = -1
//        localtion.value= -1
//        name.value = ""
//        parent.value = -1
//        description.value = ""
//    }
//}