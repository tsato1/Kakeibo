package com.kakeibo.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kakeibo.R
import com.kakeibo.data.Item
import com.kakeibo.databinding.FragmentSearchBinding
import com.kakeibo.ui.model.SearchCriteriaCard
import com.kakeibo.ui.view.RecyclerItemTouchHelper
import com.kakeibo.ui.listener.RecyclerItemTouchHelperListener
import com.kakeibo.ui.adapter.view.SearchCardListAdapter
import com.kakeibo.ui.adapter.view.SearchCardListAdapter.*
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.ui.viewmodel.ItemViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.compareDate
import com.kakeibo.util.UtilQuery
import java.math.BigDecimal
import java.util.*

class SearchFragment : Fragment(), RecyclerItemTouchHelperListener {

    companion object {
        fun newInstance(): SearchFragment {
            val tabFragment3 = SearchFragment()
            val args = Bundle()
            tabFragment3.arguments = args
            return tabFragment3
        }
    }

    private lateinit var _viewRoot: CoordinatorLayout
    private lateinit var _instructionTextView: TextView
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _searchAdapter: SearchCardListAdapter
    private lateinit var _searchCriteria: Array<String>
    private lateinit var _lstSearchCriteriaCards: ArrayList<SearchCriteriaCard> // for cards displayed
    private lateinit var _lstChoices: ArrayList<String> // for choices shown in dialog upon tapping fab

    private lateinit var _allItems: List<Item>
    private var _query = Query()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _searchCriteria = resources.getStringArray(R.array.search_criteria)
        _lstSearchCriteriaCards = ArrayList()
        _lstChoices = ArrayList(listOf(*_searchCriteria))

        val itemViewModel: ItemViewModel by activityViewModels()
        val categoryViewModel: CategoryViewModel by activityViewModels()
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        val view = binding.root

        itemViewModel.all.observe(viewLifecycleOwner, {
            _allItems = it
        })

        _viewRoot = view.findViewById(R.id.col_root_fragment3)
        _instructionTextView = view.findViewById(R.id.txv_inst_search)

        _recyclerView = view.findViewById(R.id.rcv_search_criteria)
        _searchAdapter = SearchCardListAdapter(_lstSearchCriteriaCards)
        _recyclerView.adapter = _searchAdapter
        _recyclerView.layoutManager = LinearLayoutManager(context)
        _recyclerView.itemAnimator = DefaultItemAnimator()
        categoryViewModel.all.observe(viewLifecycleOwner, { all ->

        })
        val ithCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(ithCallback).attachToRecyclerView(_recyclerView)

        return view
    }

    override fun onResume() {
        super.onResume()
        if (_lstSearchCriteriaCards.size == 0) {
            _instructionTextView.visibility = View.VISIBLE
        } else {
            _instructionTextView.visibility = View.INVISIBLE
        }
        val indexAmountRangeCard = _lstSearchCriteriaCards.indexOf(SearchCriteriaCard(SearchCriteriaCard.TYPE_AMOUNT_RANGE, 0))
        if (indexAmountRangeCard > -1) {
            val viewHolder = _recyclerView.findViewHolderForAdapterPosition(indexAmountRangeCard)
            if (viewHolder is ViewHolderAmountRange) {
                viewHolder.edtMin.setText("")
                viewHolder.edtMax.setText("")
            }
        }
        _searchAdapter.notifyDataSetChanged()
    }

    private fun checkBeforeSearch(): Boolean {
        if (_lstSearchCriteriaCards.size == 0) {
            Toast.makeText(requireContext(),
                    resources.getString(R.string.err_no_search_criteria_found),
                    Toast.LENGTH_SHORT).show()
            return false
        }
        val indexDateRangeCard = _lstSearchCriteriaCards.indexOf(SearchCriteriaCard(SearchCriteriaCard.TYPE_DATE_RANGE, 0))
        if (indexDateRangeCard > -1) {
            val viewHolder = _recyclerView.findViewHolderForAdapterPosition(indexDateRangeCard)
            if (viewHolder is ViewHolderDateRange) {
                val fDate = viewHolder.btnFrom.text.toString()
                val tDate = viewHolder.btnTo.text.toString()
                if ("" == fDate) {
                    Toast.makeText(activity, resources.getString(R.string.err_please_choose_from_date), Toast.LENGTH_SHORT).show()
                    return false
                }
                if ("" == tDate) {
                    Toast.makeText(activity, resources.getString(R.string.err_please_choose_to_date), Toast.LENGTH_SHORT).show()
                    return false
                }
                if (compareDate(fDate, tDate, MainActivity.dateFormat) == -1) {
                    Toast.makeText(activity, resources.getString(R.string.err_from_date_older), Toast.LENGTH_SHORT).show()
                    return false
                }
                _query.flagEventDate = true
                _query.fromEventDate = UtilDate.getDBDate(fDate, MainActivity.dateFormat)
                _query.toEventDate = UtilDate.getDBDate(tDate, MainActivity.dateFormat)
            }
        }
        val indexAmountRangeCard = _lstSearchCriteriaCards.indexOf(SearchCriteriaCard(SearchCriteriaCard.TYPE_AMOUNT_RANGE, 0))
        if (indexAmountRangeCard > -1) {
            val viewHolder = _recyclerView.findViewHolderForAdapterPosition(indexAmountRangeCard)
            if (viewHolder is ViewHolderAmountRange) {
                val min = viewHolder.edtMin.text.toString()
                val max = viewHolder.edtMax.text.toString()
                if ("" == min) {
                    Toast.makeText(activity, resources.getString(R.string.err_please_enter_min_amount), Toast.LENGTH_SHORT).show()
                    return false
                }
                if ("" == max) {
                    Toast.makeText(activity, resources.getString(R.string.err_please_enter_max_amount), Toast.LENGTH_SHORT).show()
                    return false
                }
                if ("0" == max || "0.0" == max || "0.00" == max || "0.000" == max) {
                    Toast.makeText(activity, resources.getString(R.string.err_max_amount_cannot_be_0), Toast.LENGTH_SHORT).show()
                    return false
                }
                val bigMin = BigDecimal(min)
                val bigMax = BigDecimal(max)
                if (bigMin > bigMax) {
                    Toast.makeText(activity, resources.getString(R.string.err_min_amount_greater), Toast.LENGTH_SHORT).show()
                    return false
                }
                _query.flagAmount = true
                _query.fromAmount = bigMin
                _query.toAmount = bigMax
            }
        }
        val indexCategoryCard = _lstSearchCriteriaCards.indexOf(SearchCriteriaCard(SearchCriteriaCard.TYPE_CATEGORY, 0))
        if (indexCategoryCard > -1) {
            val viewHolder = _recyclerView.findViewHolderForAdapterPosition(indexCategoryCard)
            if (viewHolder is ViewHolderCategory) {
                val category = viewHolder.btnCategory.text.toString()
                val categoryCode = viewHolder.selectedCategoryCode
                if ("" == category) {
                    Toast.makeText(activity, resources.getString(R.string.err_please_select_category), Toast.LENGTH_SHORT).show()
                    return false
                }
                _query.flagCategory = true
                _query.categoryCode = categoryCode
            }
        }
        val indexMemoCard = _lstSearchCriteriaCards.indexOf(SearchCriteriaCard(SearchCriteriaCard.TYPE_MEMO, 0))
        if (indexMemoCard > -1) {
            val viewHolder = _recyclerView.findViewHolderForAdapterPosition(indexMemoCard)
            if (viewHolder is ViewHolderMemo) {
                val memo = viewHolder.edtMemo.text.toString()
                if ("" == memo) {
                    Toast.makeText(activity, resources.getString(R.string.err_memo_empty), Toast.LENGTH_SHORT).show()
                    return false
                }
                _query.flagMemo = true
                _query.memo = memo
            }
        }
        return true
    }

    override fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        var name = ""
        if (viewHolder is ViewHolderDateRange) {
            name = resources.getString(R.string.date_range)
            _query.flagEventDate = false
            _searchAdapter
            _lstChoices.add(0, _searchCriteria[0])
        } else if (viewHolder is ViewHolderAmountRange) {
            name = resources.getString(R.string.amount_range)
            _query.flagAmount = false
            if (_lstChoices.size == 0) {
                _lstChoices.add(_searchCriteria[1])
            } else {
                _lstChoices.add(1, _searchCriteria[1])
            }
        } else if (viewHolder is ViewHolderCategory) {
            name = resources.getString(R.string.category)
            _query.flagCategory = false
            if (_lstChoices.size <= 1) {
                _lstChoices.add(_searchCriteria[2])
            } else {
                _lstChoices.add(2, _searchCriteria[2])
            }
        } else if (viewHolder is ViewHolderMemo) {
            name = resources.getString(R.string.memo)
            _query.flagMemo = false
            if (_lstChoices.size <= 2) {
                _lstChoices.add(_searchCriteria[3])
            } else {
                _lstChoices.add(3, _searchCriteria[3])
            }
        }
        val cardItem = _lstSearchCriteriaCards[viewHolder.adapterPosition]
        val deleteIndex = viewHolder.adapterPosition
        _searchAdapter.removeItem(deleteIndex)
        if (_lstSearchCriteriaCards.size == 0) {
            _instructionTextView.visibility = View.VISIBLE
        }
        val snackbar = Snackbar.make(_viewRoot, name + resources.getString(R.string.msg_card_is_removed), Snackbar.LENGTH_LONG)
        snackbar.setAction(getString(R.string.undo)) {
            _searchAdapter.restoreItem(cardItem, deleteIndex)
            _instructionTextView.visibility = View.INVISIBLE
            for (i in _lstChoices.indices) {
                if (_lstChoices[i] == _searchCriteria[cardItem.type]) _lstChoices.removeAt(i)
            }
        }.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
        snackbar.show()
    }

    fun addCriteria() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.add_search_criterion))
        builder.setIcon(R.mipmap.ic_mikan)
        builder.setItems(_lstChoices.toTypedArray()) { _, which ->
            val str = _lstChoices.removeAt(which)
            var selected = 0
            for (i in _searchCriteria.indices) {
                if (_searchCriteria[i] == str) selected = i
            }
            val card = SearchCriteriaCard(selected, 0)
            _lstSearchCriteriaCards.add(card)
            _searchAdapter.notifyDataSetChanged()
            _instructionTextView.visibility = View.INVISIBLE
        }
        builder.setNegativeButton(R.string.cancel) { _, _ -> }
        builder.show()
    }

    fun doSearch() {
        if (checkBeforeSearch()) {
            _query.resetFlags()

            for (card: SearchCriteriaCard in _lstSearchCriteriaCards) {
                if (card.type == SearchCriteriaCard.TYPE_DATE_RANGE) {
                    _query.flagEventDate = true
                }
                if (card.type == SearchCriteriaCard.TYPE_AMOUNT_RANGE) {
                    _query.flagAmount = true
                }
                if (card.type == SearchCriteriaCard.TYPE_CATEGORY) {
                    _query.flagCategory = true
                }
                if (card.type == SearchCriteriaCard.TYPE_MEMO) {
                    _query.flagMemo = true
                }
            }

            val result = UtilQuery.query(_allItems, _query)

            if (result.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show()
                return
            }

            (requireActivity() as MainActivity).onSearch(_query)
        }
    }
}