//package com.kakeibo.ui.settings.category.replace
//
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.kakeibo.R
//import com.kakeibo.feature_settings.settings_category.domain.model.Category
//import com.kakeibo.databinding.FragmentSettingsCategoryReplaceBinding
//import com.kakeibo.feature_settings.presentation.category_reorder.RecyclerViewAdapter
//import com.kakeibo.feature_settings.presentation.category_reorder.CategoryClickListener
//import com.kakeibo.feature_settings.settings_category.presentation.CategoryViewModel
//
//
//class CategoryReplaceRemoveFragment : Fragment(), CategoryClickListener {
//
//    companion object {
//        const val TAG_INT = 0
//
//        fun newInstance(): CategoryReplaceRemoveFragment {
//            val fragment = CategoryReplaceRemoveFragment()
//            val args = Bundle()
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    private lateinit var _backButton: Button
//    private lateinit var _nextButton: Button
//
//    private val _medium: Medium by activityViewModels()
//    private val _categoryViewModel: CategoryViewModel by activityViewModels()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val binding = FragmentSettingsCategoryReplaceBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        /*** find views  */
//        val titleTextView: TextView = view.findViewById(R.id.txv_title)
//        titleTextView.setText(R.string.hide_categories)
//        _backButton = view.findViewById(R.id.btn_back)
//        _nextButton = view.findViewById(R.id.btn_next)
//        _backButton.setOnClickListener(ItemClickListener())
//        _nextButton.setOnClickListener(ItemClickListener())
//
//        val list = ArrayList<GridItem>()
//
//        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_grid)
//        val recyclerViewAdapter = RecyclerViewAdapter(list, this)
////        recyclerView.layoutManager = GridLayoutManager(activity, CategoryReplaceActivity.numColumns)
//        recyclerView.adapter = recyclerViewAdapter
//        _categoryViewModel.dsp.observe(viewLifecycleOwner, {
//            list.clear()
//            it.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }
//        })
//
//        return view
//    }
//
//    internal inner class ItemClickListener : View.OnClickListener {
//        override fun onClick(view: View) {
//            when (view.id) {
////                R.id.btn_back -> (activity as CategoryReplaceActivity).onBackPressed(TAG_INT)
//                R.id.btn_next -> {
//                    if (_medium.removedCategoryList.size <= 0) {
//                        Toast.makeText(requireContext(), "Please remove at least one category", Toast.LENGTH_LONG).show()
//                        return
//                    }
//
//                    val sum = _medium.newCategoryList + _medium.removedCategoryList
//                    val out = sum.groupBy { it.id }
//                            .filter { it.value.size == 1 }
//                            .flatMap { it.value }
//                    _medium.newCategoryList.clear()
//                    _medium.newCategoryList.addAll(out)
//
////                    (activity as CategoryReplaceActivity).onNextPressed(TAG_INT)
//                }
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        requireActivity().finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            requireActivity().onBackPressed()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    override fun onCategoryClicked(view: View, category: Category) {
//        val imv: ImageView = view.findViewById(R.id.imv_category_remove)
//
//        if (_medium.removedCategoryList.contains(category)) {
//            _medium.removedCategoryList.remove(category)
//            imv.visibility = View.GONE
//        } else {
//            _medium.removedCategoryList.add(category)
//            imv.visibility = View.VISIBLE
//        }
//    }
//}