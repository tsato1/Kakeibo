package com.kakeibo.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.FragmentReportBinding
import com.kakeibo.ui.adapter.view.ExpandableListAdapter
import com.kakeibo.ui.adapter.view.ReportCListAdapter
import com.kakeibo.ui.listener.SimpleClickListener
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.util.*
import java.math.BigDecimal

/**
 * Created by T on 2015/09/14.
 */
class ReportFragment : Fragment(), SimpleClickListener {

    companion object {
        private const val SWIPE_REFRESH_MILLI_SECOND = 400

        fun newInstance(): ReportFragment {
            val tabFragment2 = ReportFragment()
            val args = Bundle()
            tabFragment2.arguments = args
            return tabFragment2
        }
    }

    private val _itemStatusViewModel: ItemStatusViewModel by activityViewModels()
    private val _categoryStatusViewModel: CategoryStatusViewModel by activityViewModels()
    private val _medium: Medium by activityViewModels()

    private lateinit var _srlReload: SwipeRefreshLayout
    private lateinit var _btnClose: ImageButton
    private lateinit var _reportA: LinearLayout
    private lateinit var _reportC: LinearLayout
    private lateinit var _reportD: LinearLayout

    private lateinit var _allItems: List<ItemStatus>
    private lateinit var _itemsThisMonth: List<ItemStatus>
    private var _result: List<ItemStatus> = listOf()
    private lateinit var _expandableListAdapter: ExpandableListAdapter
    private lateinit var _incomeListAdapter: ReportCListAdapter
    private lateinit var _expenseListAdapter: ReportCListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val fragmentBinding = FragmentReportBinding.inflate(inflater, container, false)
        fragmentBinding.lifecycleOwner = this
        fragmentBinding.itemStatusViewModel = _itemStatusViewModel
        val view = fragmentBinding.root

        val reportCBinding = fragmentBinding.fragmentReportC
        reportCBinding.itemStatusViewModel = _itemStatusViewModel

        val bannerDatePickerBinding = fragmentBinding.bannerDatePicker
        bannerDatePickerBinding.medium = _medium
        bannerDatePickerBinding.clickListener = this
        bannerDatePickerBinding.itemStatusViewModel = _itemStatusViewModel

        val expandableListView: RecyclerView = view.findViewById(R.id.lsv_expandable)
        _expandableListAdapter = ExpandableListAdapter(_categoryStatusViewModel, requireContext())
        _expandableListAdapter.setItemStatusViewMode(_itemStatusViewModel)
        expandableListView.adapter = _expandableListAdapter
        expandableListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val incomeListView: RecyclerView = view.findViewById(R.id.rcv_income)
        _incomeListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_INCOME, _categoryStatusViewModel)
        incomeListView.adapter = _incomeListAdapter
        val expenseListView: RecyclerView = view.findViewById(R.id.rcv_expense)
        _expenseListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_EXPENSE, _categoryStatusViewModel)
        expenseListView.adapter = _expenseListAdapter

        _itemStatusViewModel.all.observe(viewLifecycleOwner, { all ->
            _allItems = all
        })
        _itemStatusViewModel.items.observe(viewLifecycleOwner, { allThisMonth ->
            _itemsThisMonth = allThisMonth

            val masterMap = allThisMonth
                    .groupBy { it.eventDate }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf { it.getAmount() }) }
                    .toSortedMap(compareBy<Pair<String, BigDecimal>> { it.first }.thenBy { it.second })
            _expandableListAdapter.setMasterMap(masterMap)

            val allThisMonthByCategory = allThisMonth.groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second < BigDecimal(0) })
        })

        val btnA: Button = view.findViewById(R.id.btn_a)
        val btnC: Button = view.findViewById(R.id.btn_c)
        val btnD: Button = view.findViewById(R.id.btn_d)
        _reportA = view.findViewById(R.id.fragment_report_a)
        _reportC = view.findViewById(R.id.fragment_report_c)
        _reportD = view.findViewById(R.id.fragment_report_d)
        btnA.setOnClickListener { v ->
            _reportA.visibility = VISIBLE
            _reportC.visibility = GONE
            _reportD.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_A)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnC.setOnClickListener { v ->
            _reportA.visibility = GONE
            _reportC.visibility = VISIBLE
            _reportD.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_C)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnD.setOnClickListener { v ->
            _reportA.visibility = GONE
            _reportC.visibility = GONE
            _reportD.visibility = VISIBLE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_D)
            bannerDatePickerBinding.executePendingBindings()
        }

        _srlReload = view.findViewById(R.id.srl_reload)
        _btnClose = view.findViewById(R.id.btn_exit_search_result)
        _srlReload.setOnRefreshListener {
            Handler().postDelayed({
                _srlReload.isRefreshing = false
            }, SWIPE_REFRESH_MILLI_SECOND.toLong())
        }

        return view
    }

    /* Called by MainActivity */
    fun export() {
        val message: String

        when (_medium.getCurrentlyShown()) {
            Medium.FRAGMENT_REPORT_C -> {
                message = getString(R.string.quest_export_this_report_C)
                Log.d("asdf","c")

                _result = if (_medium.getInSearchResult()) {
                    if (_result.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
                        return
                    }
                    _result.sortedBy { it.categoryCode }
                } else {
                    if (_itemsThisMonth.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
                        return
                    }
                    _itemsThisMonth.sortedBy { it.categoryCode }
                }
            }
            Medium.FRAGMENT_REPORT_D -> {
                Log.d("asdf","DDDDDDD")
                message = getString(R.string.quest_export_this_report_D)

                _result = if (_medium.getInSearchResult()) {
                    if (_result.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
                        return
                    }
                    _result.sortedBy { it.eventDate }
                } else {
                    if (_itemsThisMonth.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
                        return
                    }
                    _itemsThisMonth.sortedBy { it.eventDate }
                }
            }
            else -> return
        }

        _result.forEach { Log.d("asdf","asdfasdf = "+it.memo) }

        val dialogExport = AlertDialog.Builder(requireContext())
        dialogExport.setIcon(R.mipmap.ic_mikan)
        dialogExport.setTitle(getString(R.string.export_category))
        dialogExport.setMessage(message)
        dialogExport.setPositiveButton(R.string.yes) { _, _ ->
            val fileName = when (_medium.getCurrentlyShown()) {
                Medium.FRAGMENT_REPORT_C -> ExportActivity.FILE_ORDER_CATEGORY
                Medium.FRAGMENT_REPORT_D -> ExportActivity.FILE_ORDER_DATE
                else -> ""
            }

            UtilExport.build(requireContext(), _result, MainActivity.allCategoryMap, fileName)

            val intent = Intent(requireContext(), ExportActivity::class.java)
            intent.putExtra("REPORT_VIEW_TYPE", _medium.getCurrentlyShown())
            startActivity(intent)
        }
        dialogExport.show()
    }

    /* Called by MainActivity */
    fun focusOnSavedItem(date: String) {
        _reportA.visibility = GONE
        _reportC.visibility = GONE
        _reportD.visibility = VISIBLE
        UtilExpandableList.expandOnlySpecificDate(_expandableListAdapter, date)
        _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_D)
    }

    /* Called by MainActivity */
    fun onSearch(query: Query) {
        _btnClose.visibility = VISIBLE
        _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackground_search))

        _result = UtilQuery.query(_allItems, query)

        _itemStatusViewModel.setMutableAll(_result)
        val masterMap = _result
                .groupBy { it.eventDate }
                .mapKeys { entry -> Pair(entry.key, entry.value.sumOf { it.getAmount() }) }
                .toSortedMap(compareBy<Pair<String, BigDecimal>> { it.first }.thenBy { it.second })
        _expandableListAdapter.setMasterMap(masterMap)

        val allItemsByCategory = _result.groupBy { it.categoryCode }
                .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
        _incomeListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second > BigDecimal(0) })
        _expenseListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second < BigDecimal(0) })

        _medium.setInSearchResult(true)
    }

    /* exiting search */
    override fun onClick() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setIcon(R.mipmap.ic_mikan)
        dialog.setTitle(getString(R.string.returning_to_monthly_report))
        dialog.setMessage(getString(R.string.msg_exit_search))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            _btnClose.visibility = GONE
            _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))

            _itemStatusViewModel.setItemsThisMonth()

            val masterMap = _itemsThisMonth
                    .groupBy { it.eventDate }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf { it.getAmount() }) }
                    .toSortedMap(compareBy<Pair<String, BigDecimal>> { it.first }.thenBy { it.second })
            _expandableListAdapter.setMasterMap(masterMap)

            val allItemsThisMonthByCategory = _itemsThisMonth.groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second < BigDecimal(0) })

            _medium.setInSearchResult(false)
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_D)
            _reportA.visibility = GONE
            _reportC.visibility = GONE
            _reportD.visibility = VISIBLE
        }
        dialog.show()
    }
}