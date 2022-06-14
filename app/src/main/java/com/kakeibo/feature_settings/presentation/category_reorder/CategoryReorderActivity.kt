package com.kakeibo.feature_settings.presentation.category_reorder

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.feature_settings.domain.models.CategoryModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryReorderActivity : AppCompatActivity() {

    private val _categoryViewModel: CategoryViewModel by viewModels()

    private lateinit var _recyclerView: RecyclerView
    private lateinit var _nextBtn: Button
    private lateinit var _recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var _itemTouchHelper: ItemTouchHelper

    private val _list = ArrayList<GridItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_category_reorder)
        _categoryViewModel.load()

        /* hide home button on actionbar  */
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(false)
        }

        /* ads  */
        if (_categoryViewModel.kkbAppModelState.value.kkbAppModel.intVal2 == ConstKkbAppDB.AD_SHOW) {
            MobileAds.initialize(this) {}
            val adView: AdView = findViewById(R.id.ad_view)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        _recyclerView = findViewById(R.id.rcv_grid)
        _nextBtn = findViewById(R.id.btn_next)

        _recyclerView.layoutManager = GridLayoutManager(this, _categoryViewModel.numColumns)
        _categoryViewModel.displayedCategories.observe(this) { list ->
            _list.clear()
            list.forEach { p ->
                _list.add(GridItem.ChildItem(p._id, p))
            }
            _recyclerViewAdapter.notifyDataSetChanged()
        }

        val gridLayoutManager = _recyclerView.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (_recyclerView.adapter!!.getItemViewType(position)) {
                    GridItem.ITEM_TYPE_HEADER -> gridLayoutManager.spanCount
                    GridItem.ITEM_TYPE_PARENT, GridItem.ITEM_TYPE_CHILD -> 1
                    else -> throw Exception("unknown item type")
                }
            }
        }

        val callback = ItemMoveCallback(this, _list)
        _recyclerViewAdapter = RecyclerViewAdapter(_list, null)
        _recyclerView.adapter = _recyclerViewAdapter
        _itemTouchHelper = ItemTouchHelper(callback)
        _itemTouchHelper.attachToRecyclerView(_recyclerView)

        _nextBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setIcon(R.mipmap.ic_mikan)
            dialog.setTitle(R.string.reorder_categories)
            dialog.setMessage(R.string.quest_determine_category_order)
            dialog.setPositiveButton(R.string.yes) { _, _ ->
                Toast.makeText(this, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show()
                _categoryViewModel.onSave(
                    _list.map {
                        CategoryModel(
                            _id = it.category!!._id,
                            code = it.category.code,
                            name = it.category.name,
                            color = it.category.color,
                            sign = it.category.sign,
                            drawable = it.category.drawable,
                            image = it.category.image,
                            parent = it.category.parent,
                            description = it.category.description,
                            savedDate = it.category.savedDate
                        )
                    }
                )
                finish()
            }
            dialog.setNegativeButton(R.string.no) { _, _ -> }
            dialog.show()
        }
    }

}