package com.kakeibo.ui.settings.category.replace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.Category
import com.kakeibo.databinding.FragmentSettingsCategoryReplaceBinding
import com.kakeibo.ui.adapter.view.RecyclerViewAdapter
import com.kakeibo.ui.viewmodel.CategoryViewModel
import kotlin.collections.ArrayList

class CategoryReplaceReorderFragment : Fragment() {

    companion object {
        val TAG = CategoryReplaceReorderFragment::class.java.simpleName
        const val TAG_INT = 2

        fun newInstance(): CategoryReplaceReorderFragment {
            val fragment = CategoryReplaceReorderFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _recyclerView: RecyclerView
    private lateinit var _backButton: Button
    private lateinit var _nextButton: Button

    private val _medium: Medium by activityViewModels()
    private val _categoryViewModel: CategoryViewModel by activityViewModels()

    private val list = ArrayList<GridItem>()
    private lateinit var _recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var _itemTouchHelper: ItemTouchHelper

    private lateinit var _eventClickListener: EventClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSettingsCategoryReplaceBinding.inflate(inflater, container, false)
        val view = binding.root

        _eventClickListener = context as EventClickListener

        findViews(view)

        _medium.newCategoryList.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }

        val numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns)
        _recyclerView.layoutManager = GridLayoutManager(activity, numColumns)
        _categoryViewModel.dsp.observe(viewLifecycleOwner, {
            list.clear()
            it.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }
        })

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

        val callback = ItemMoveCallback(requireContext(), list)
        _recyclerViewAdapter = RecyclerViewAdapter(list, null)
        _recyclerView.adapter = _recyclerViewAdapter
        _itemTouchHelper = ItemTouchHelper(callback)
        _itemTouchHelper.attachToRecyclerView(_recyclerView)

        return view
    }

    private fun findViews(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.txv_title)
        titleTextView.setText(R.string.reorder_categories_for_display)
        val descriptionTextView: TextView = view.findViewById(R.id.txv_description)
        descriptionTextView.setText(R.string.inst_long_tap_to_move_icons)

        _backButton = view.findViewById(R.id.btn_back)
        _nextButton = view.findViewById(R.id.btn_next)
        _recyclerView = view.findViewById(R.id.rcv_grid)
        _nextButton.text = getString(R.string.done)
        _backButton.setOnClickListener(ItemClickListener())
        _nextButton.setOnClickListener(ItemClickListener())
    }

    fun setGridItems() {
        list.clear()
        _medium.newCategoryList.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }
        _recyclerViewAdapter.notifyDataSetChanged()
    }

    internal inner class ItemClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_back -> {
                    _medium.newCategoryList.removeAll(_medium.addedCategoryList)
                    _recyclerViewAdapter.notifyDataSetChanged()
                    _eventClickListener.onBackPressed(TAG_INT)
                }
                R.id.btn_next -> {
                    val list = _recyclerViewAdapter.getList()
                    val out: MutableList<Category> = ArrayList()
                    for (item in list) {
                        val categoryStatus = item.category as Category
                        out.add(categoryStatus)
                    }

                    _medium.newCategoryList.addAll(out)
                    _eventClickListener.onNextPressed(TAG_INT)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}