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
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.databinding.FragmentSettingsCategoryReplaceBinding
import com.kakeibo.ui.adapter.view.RecyclerViewAdapter
import com.kakeibo.ui.listener.CategoryClickListener
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.util.UtilCategory

class CategoryReplaceAddFragment : Fragment(), CategoryClickListener {

    companion object {
        const val TAG_INT = 1

        fun newInstance(): CategoryReplaceAddFragment {
            val fragment = CategoryReplaceAddFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _remainingCount = 0 // remaining count for addition

    private lateinit var _backButton: Button
    private lateinit var _nextButton: Button
    private lateinit var _descriptionTextView: TextView

    private val _medium: Medium by activityViewModels()

    private val _categoryStatusViewModel: CategoryStatusViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSettingsCategoryReplaceBinding.inflate(inflater, container, false)
        val view = binding.root
        findViews(view)

        val list = ArrayList<GridItem>()

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_grid)
        recyclerView.layoutManager = GridLayoutManager(activity, CategoryReplaceActivity.numColumns)
        recyclerView.adapter = RecyclerViewAdapter(list, this)
        _categoryStatusViewModel.nonDsp.observe(viewLifecycleOwner, {
            list.clear()
            it.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }
        })

        return view
    }

    private fun findViews(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.txv_title)
        titleTextView.setText(R.string.display_categories)

        _descriptionTextView = view.findViewById(R.id.txv_description)
        val remainingCount = UtilCategory.NUM_MAX_DSP_CATEGORIES - _medium.newCategoryList.size
        val tmp = requireContext().getString(R.string.remaining_spots_colon) + remainingCount
        _descriptionTextView.text = tmp

        _backButton = view.findViewById(R.id.btn_back)
        _nextButton = view.findViewById(R.id.btn_next)
        _backButton.setOnClickListener(ItemClickListener())
        _nextButton.setOnClickListener(ItemClickListener())
    }

    fun setRemainingCount() {
        _remainingCount = UtilCategory.NUM_MAX_DSP_CATEGORIES - _medium.newCategoryList.size - _medium.addedCategoryList.size
        val str = getString(R.string.remaining_spots_colon) + _remainingCount
        _descriptionTextView.text = str
    }

    internal inner class ItemClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_back -> {
                    _medium.newCategoryList.addAll(_medium.removedCategoryList)
                    (activity as CategoryReplaceActivity).onBackPressed(TAG_INT)
                }
                R.id.btn_next -> {
                    if (_medium.newCategoryList.size + _medium.addedCategoryList.size > UtilCategory.NUM_MAX_DSP_CATEGORIES) {
                        Toast.makeText(requireContext(), "You cannot exceed the MAX count: " + UtilCategory.NUM_MAX_DSP_CATEGORIES, Toast.LENGTH_LONG).show()
                        return
                    }
                    _medium.newCategoryList.addAll(_medium.addedCategoryList)
                    (activity as CategoryReplaceActivity).onNextPressed(TAG_INT)
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

    override fun onCategoryClicked(view: View, category: CategoryStatus) {
        val imv: ImageView = view.findViewById(R.id.imv_category_add)

        if (_medium.addedCategoryList.contains(category)) {
            _medium.addedCategoryList.remove(category)
            imv.visibility = View.GONE
            _remainingCount++
        } else {
            if (_remainingCount <= 0) {
                Toast.makeText(requireActivity(), getString(R.string.err_cannot_add_category) +
                        "(=" + UtilCategory.NUM_MAX_DSP_CATEGORIES + ")", Toast.LENGTH_LONG).show()
            } else {
                _medium.addedCategoryList.add(category)
                imv.visibility = View.VISIBLE
                _remainingCount--
            }
        }
        val tmp = getString(R.string.remaining_spots_colon) + _remainingCount
        _descriptionTextView.text = tmp
    }
}