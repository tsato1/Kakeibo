//package com.kakeibo.ui.settings.category.edit
//
//import android.app.AlertDialog
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.ContextMenu
//import android.view.ContextMenu.ContextMenuInfo
//import android.view.MenuItem
//import android.view.View
//import android.view.View.OnCreateContextMenuListener
//import android.widget.*
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.ObservableArrayList
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.MobileAds
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.kakeibo.R
//import com.kakeibo.feature_settings.settings_category.domain.model.Category
//import com.kakeibo.databinding.ActivitySettingsCustomCategoryListBinding
//import com.kakeibo.ui.adapter.view.CategoryListAdapter
//import com.kakeibo.feature_settings.settings_category.presentation.CategoryViewModel
//import com.kakeibo.feature_item.presentation.item_list.ItemViewModel
//import com.kakeibo.ui.viewmodel.KkbAppViewModel
//import com.kakeibo.util.UtilCategory
//
//class CustomCategoryListActivity : AppCompatActivity() {
//
//    companion object {
//        private const val MENU_ITEM_ID_DELETE = 0
//        private const val MENU_ITEM_ID_EDIT = 1
//        const val EXTRA_KEY_CATEGORY_ID = "CATEGORY_ID"
//        const val EXTRA_KEY_CATEGORY_CODE = "CATEGORY_CODE"
//    }
//
//    private lateinit var _context: Context
//    private lateinit var _btnBack: Button
//    private lateinit var _fabAdd: FloatingActionButton
//
//    private val _customCategoryList = ObservableArrayList<Category>()
//    private lateinit var _categoryListAdapter: CategoryListAdapter
//    private val _startForResult = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        _categoryListAdapter.notifyDataSetChanged()
//    }
//
//    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
//    private val _itemViewModel: ItemViewModel by viewModels()
//    private val _categoryViewModel: CategoryViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivitySettingsCustomCategoryListBinding.inflate(layoutInflater)
//        binding.list = _customCategoryList
//        setContentView(binding.root)
//        _context = this
//
//        /* hide home button on actionbar */
//        if (supportActionBar != null) {
//            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
//            supportActionBar!!.setHomeButtonEnabled(false)
//        }
//
//        /* ads */
//        _kkbAppViewModel.all.observe(this, {
//            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads
//
//            if (showAds) {
//                MobileAds.initialize(this) {}
//                val adView: AdView = findViewById(R.id.ad_container)
//                val adRequest = AdRequest.Builder().build()
//                adView.loadAd(adRequest)
//            }
//        })
//
//        /* findViews */
//        _btnBack = findViewById(R.id.btn_back)
//        _fabAdd = findViewById(R.id.fab_add)
//        _btnBack.setOnClickListener { onBackPressed() }
//        _fabAdd.setOnClickListener {
//            when (_categoryViewModel.canCreateNewCustomCategory()) {
//                -2 -> {
//                    val s = getString(R.string.err_reached_max_count_colon) +
//                            UtilCategory.NUM_MAX_CUSTOM_CATEGORY + "\n" +
//                            getString(R.string.msg_delete_some_categories) //todo 5 for ordinary version, 100 for paid, 1000 for b2b
//                    Toast.makeText(this, s, Toast.LENGTH_LONG).show()
//                }
//                -1 -> {
//                    Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
//                }
//                1 -> {
//                    val intent = Intent(_context, CustomCategoryActivity::class.java)
//                    _startForResult.launch(intent)
//                }
//            }
//        }
//
//        val categoryListView: ListView = findViewById(R.id.lsv_custom_categories)
//        categoryListView.onItemClickListener = ItemClickListener()
//        categoryListView.setOnCreateContextMenuListener(ItemContextClickListener())
//        _categoryListAdapter = CategoryListAdapter(_context, 0, _customCategoryList)
//        categoryListView.adapter = _categoryListAdapter
//        _categoryViewModel.custom.observe(this, {
//            _customCategoryList.clear()
//            _customCategoryList.addAll(it)
//            _categoryListAdapter.notifyDataSetChanged()
//        })
//    }
//
//    internal inner class ItemClickListener : AdapterView.OnItemClickListener {
//        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
//            val message = getString(R.string.updated_on_colon) + _customCategoryList[position].savedDate
//            val dialog = AlertDialog.Builder(_context)
//            dialog.setIcon(R.mipmap.ic_mikan)
//            dialog.setTitle(_customCategoryList[position].name)
//            dialog.setMessage(message)
//            dialog.setPositiveButton(R.string.ok) { _, _ -> }
//            dialog.create()
//            dialog.show()
//        }
//    }
//
//    internal class ItemContextClickListener : OnCreateContextMenuListener {
//        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
//            menu.setHeaderIcon(R.mipmap.ic_mikan)
//            menu.add(0, MENU_ITEM_ID_EDIT, 0, R.string.edit)
//            menu.add(0, MENU_ITEM_ID_DELETE, 1, R.string.delete)
//        }
//    }
//
//    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
//        val info = menuItem.menuInfo as AdapterView.AdapterContextMenuInfo
//        val categoryStatus = _categoryListAdapter.getItem(info.position)
//
//        when (menuItem.itemId) {
//            MENU_ITEM_ID_EDIT -> {
//                categoryStatus?.let {
//                    val intent = Intent(_context, CustomCategoryActivity::class.java)
//                    intent.putExtra(EXTRA_KEY_CATEGORY_ID, categoryStatus.id)
//                    intent.putExtra(EXTRA_KEY_CATEGORY_CODE, categoryStatus.code)
//                    _startForResult.launch(intent)
//                }
//            }
//            MENU_ITEM_ID_DELETE -> {
//                categoryStatus?.let {
//                    if (_itemViewModel.isCategoryAlreadyUsed(it.code)) {
//                        val str1 = getString(R.string.msg_custom_category_already_in_use)
//                        val str2 = getString(R.string.msg_delete_kkb_items_first)
//                        Toast.makeText(_context, str1 + str2, Toast.LENGTH_LONG).show()
//                        return false
//                    }
//
//                    val dialog = AlertDialog.Builder(this)
//                    dialog.setIcon(R.mipmap.ic_mikan)
//                    dialog.setTitle(R.string.delete)
//                    dialog.setMessage(R.string.quest_do_you_want_to_delete_item)
//                    dialog.setPositiveButton(R.string.yes) { _, _ ->
//                        _categoryViewModel.delete(categoryStatus.id)
//                        Toast.makeText(this, R.string.msg_category_successfully_deleted, Toast.LENGTH_LONG).show()
//                    }
//                    dialog.setNegativeButton(R.string.no) { _, _ -> }
//                    dialog.create()
//                    dialog.show()
//                }
//            }
//        }
//        return super.onContextItemSelected(menuItem)
//    }
//}