package com.kakeibo.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.kakeibo.data.Category
import com.kakeibo.data.Item
import com.kakeibo.databinding.FragmentReportBinding
import com.kakeibo.ui.adapter.view.ExpandableListAdapter
import com.kakeibo.ui.adapter.view.ReportCListAdapter
import com.kakeibo.ui.listener.SimpleClickListener
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.ui.viewmodel.ItemViewModel
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

    private val _itemViewModel: ItemViewModel by activityViewModels()
    private val _categoryViewModel: CategoryViewModel by activityViewModels()
    private val _medium: Medium by activityViewModels()

    private lateinit var _srlReload: SwipeRefreshLayout
    private lateinit var _btnClose: ImageButton
    private lateinit var _reportCategoryYearly: LinearLayout
    private lateinit var _reportDateYearly: LinearLayout
    private lateinit var _reportCategoryMonthly: LinearLayout
    private lateinit var _reportDateMonthly: LinearLayout

    private lateinit var _allItems: List<Item>
    private lateinit var _itemsThisMonth: List<Item>
    private lateinit var _allCategoriesMap: Map<Int, Category>
    private var _result: List<Item> = listOf()
    private lateinit var _expandableListAdapter: ExpandableListAdapter
    private lateinit var _incomeListAdapter: ReportCListAdapter
    private lateinit var _expenseListAdapter: ReportCListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        val fragmentBinding = FragmentReportBinding.inflate(inflater, container, false)
        fragmentBinding.lifecycleOwner = this
        fragmentBinding.itemViewModel = _itemViewModel
        val view = fragmentBinding.root

        val reportCategoryMonthlyBinding = fragmentBinding.fragmentReportCategoryMonthly
        reportCategoryMonthlyBinding.lifecycleOwner = this
        reportCategoryMonthlyBinding.itemStatusViewModel = _itemViewModel
        reportCategoryMonthlyBinding.categoryStatusViewModel = _categoryViewModel

        val reportCategoryYearlyBinding = fragmentBinding.fragmentReportCategoryYearly
        reportCategoryYearlyBinding.lifecycleOwner = this
        reportCategoryYearlyBinding.itemStatusViewModel = _itemViewModel
        reportCategoryYearlyBinding.categoryStatusViewModel = _categoryViewModel

        val bannerDatePickerBinding = fragmentBinding.bannerDatePicker
        bannerDatePickerBinding.lifecycleOwner = this
        bannerDatePickerBinding.medium = _medium
        bannerDatePickerBinding.clickListener = this
        bannerDatePickerBinding.itemViewModel = _itemViewModel

        val expandableListView: RecyclerView = view.findViewById(R.id.lsv_expandable)
        _expandableListAdapter = ExpandableListAdapter(_categoryViewModel, this)
        _expandableListAdapter.setItemStatusViewMode(_itemViewModel)
        expandableListView.adapter = _expandableListAdapter
        expandableListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val incomeListView: RecyclerView = view.findViewById(R.id.rcv_income)
        _incomeListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_INCOME, _categoryViewModel, this)
        incomeListView.adapter = _incomeListAdapter
        val expenseListView: RecyclerView = view.findViewById(R.id.rcv_expense)
        _expenseListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_EXPENSE, _categoryViewModel, this)
        expenseListView.adapter = _expenseListAdapter

        _itemViewModel.all.observe(viewLifecycleOwner, {
            _allItems = it
        })
        _categoryViewModel.allMap.observe(viewLifecycleOwner, {
            _allCategoriesMap = it
        })
        _itemViewModel.items.observe(viewLifecycleOwner, { allThisMonth ->
            _itemsThisMonth = allThisMonth

            val masterMap = allThisMonth
                    .groupBy { it.eventDate }
                    .mapKeys { entry ->
                        ExpandableListRowModel.Header(
                                entry.key,
                                entry.value.asSequence().filter{ it.getAmount() > BigDecimal(0) }.sumOf { it.getAmount() },
                                entry.value.asSequence().filter{ it.getAmount() < BigDecimal(0) }.sumOf { it.getAmount() }) }
                    .toSortedMap(compareBy { it.date })
            _expandableListAdapter.setMasterMap(masterMap)

            val allThisMonthByCategory = allThisMonth
                    .groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second < BigDecimal(0) })
        })

        val btnA: Button = view.findViewById(R.id.btn_category_yearly)
        val btnB: Button = view.findViewById(R.id.btn_date_yearly)
        val btnC: Button = view.findViewById(R.id.btn_category_monthly)
        val btnD: Button = view.findViewById(R.id.btn_date_monthly)
        _reportCategoryYearly = view.findViewById(R.id.fragment_report_category_yearly)
        _reportDateYearly = view.findViewById(R.id.fragment_report_date_yearly)
        _reportCategoryMonthly = view.findViewById(R.id.fragment_report_category_monthly)
        _reportDateMonthly = view.findViewById(R.id.fragment_report_date_monthly)
        btnA.setOnClickListener { v ->
            _reportCategoryYearly.visibility = VISIBLE
            _reportDateYearly.visibility = GONE
            _reportCategoryMonthly.visibility = GONE
            _reportDateMonthly.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_YEARLY)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnB.setOnClickListener { v ->
            _reportCategoryYearly.visibility = GONE
            _reportDateYearly.visibility = VISIBLE
            _reportCategoryMonthly.visibility = GONE
            _reportDateMonthly.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_YEARLY)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnC.setOnClickListener { v ->
            _reportCategoryYearly.visibility = GONE
            _reportDateYearly.visibility = GONE
            _reportCategoryMonthly.visibility = VISIBLE
            _reportDateMonthly.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnD.setOnClickListener { v ->
            _reportCategoryYearly.visibility = GONE
            _reportDateYearly.visibility = GONE
            _reportCategoryMonthly.visibility = GONE
            _reportDateMonthly.visibility = VISIBLE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
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
            Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY -> {
                message = getString(R.string.quest_export_this_report_C)

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
            Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
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

        val dialogExport = AlertDialog.Builder(requireContext())
        dialogExport.setIcon(R.mipmap.ic_mikan)
        dialogExport.setTitle(getString(R.string.export_category))
        dialogExport.setMessage(message)
        dialogExport.setPositiveButton(R.string.yes) { _, _ ->
            val fileName = when (_medium.getCurrentlyShown()) {
                Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY -> ExportActivity.FILE_ORDER_CATEGORY
                Medium.FRAGMENT_REPORT_DATE_MONTHLY -> ExportActivity.FILE_ORDER_DATE
                else -> ""
            }

            UtilExport.build(requireContext(), _result, _allCategoriesMap, fileName)

            val intent = Intent(requireContext(), ExportActivity::class.java)
            intent.putExtra("REPORT_VIEW_TYPE", _medium.getCurrentlyShown())
            startActivity(intent)
        }
        dialogExport.show()
    }

    /* Called by MainActivity */
    fun focusOnSavedItem(date: String) {
        _reportCategoryYearly.visibility = GONE
        _reportCategoryMonthly.visibility = GONE
        _reportDateMonthly.visibility = VISIBLE
        UtilExpandableList.expandOnlySpecificDate(_expandableListAdapter, date)
        _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
    }

    /* Called by MainActivity */
    fun onSearch(query: Query) {
        _btnClose.visibility = VISIBLE
        _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_background_search))

        _result = UtilQuery.query(_allItems, query)

        _itemViewModel.setMutableAll(_result)
        val masterMap = _result
                .groupBy { it.eventDate }
                .mapKeys { entry ->
                    ExpandableListRowModel.Header(
                            entry.key,
                            entry.value.asSequence().filter{ it.getAmount() > BigDecimal(0) }.sumOf { it.getAmount() },
                            entry.value.asSequence().filter{ it.getAmount() < BigDecimal(0) }.sumOf { it.getAmount() }) }
                .toSortedMap(compareBy { it.date })
        _expandableListAdapter.setMasterMap(masterMap)

        val allItemsByCategory = _result
                .groupBy { it.categoryCode }
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
            _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_background))

            _itemViewModel.setItemsThisMonth()

            val masterMap = _itemsThisMonth
                    .groupBy { it.eventDate }
                    .mapKeys { entry ->
                        ExpandableListRowModel.Header(
                                entry.key,
                                entry.value.asSequence().filter{ it.getAmount() > BigDecimal(0) }.sumOf { it.getAmount() },
                                entry.value.asSequence().filter{ it.getAmount() < BigDecimal(0) }.sumOf { it.getAmount() }) }
                    .toSortedMap(compareBy { it.date })
            _expandableListAdapter.setMasterMap(masterMap)

            val allItemsThisMonthByCategory = _itemsThisMonth
                    .groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second < BigDecimal(0) })

            _medium.setInSearchResult(false)
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
            _reportCategoryYearly.visibility = GONE
            _reportCategoryMonthly.visibility = GONE
            _reportDateMonthly.visibility = VISIBLE
        }
        dialog.show()
    }
}