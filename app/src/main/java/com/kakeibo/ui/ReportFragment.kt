package com.kakeibo.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
import com.kakeibo.ui.listener.ButtonClickListener
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.util.*
import java.math.BigDecimal

/**
 * Created by T on 2015/09/14.
 */
class ReportFragment : Fragment(), ButtonClickListener {

    companion object {
        private const val SWIPE_REFRESH_MILLI_SECOND = 400

        var REPORT_BY_DATE = 0 //todo use medium in mainactivity
        var REPORT_BY_CATEGORY = 1
        var REPORT_BY_AMOUNT = 2

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
    private lateinit var _reportC: NestedScrollView
    private lateinit var _reportD: NestedScrollView

    private lateinit var _allItems: List<ItemStatus>
    private lateinit var _itemsThisMonth: List<ItemStatus>
    private var _result: List<ItemStatus> = listOf()
    private lateinit var _expandableListAdapter: ExpandableListAdapter
    private val _expandableList: MutableList<ExpandableListRowModel> = ArrayList()
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
        _expandableListAdapter.setExpandableList(_expandableList)
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
            _expandableList.clear()
            _expandableListAdapter.setMasterMap(masterMap)

            val allThisMonthByCategory = allThisMonth.groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second < BigDecimal(0) })
        })

        val btnC: Button = view.findViewById(R.id.btn_c)
        val btnD: Button = view.findViewById(R.id.btn_d)
        _reportC = view.findViewById(R.id.fragment_report_c)
        _reportD = view.findViewById(R.id.fragment_report_d)
        btnC.setOnClickListener { v ->
            _reportC.visibility = VISIBLE
            _reportD.visibility = GONE
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_C)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnD.setOnClickListener { v ->
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
        if (_result.isEmpty()) {
            Toast.makeText(requireContext(), R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
            return
        }

        val message = ""
        when (_medium.getCurrentlyShown()) {
            Medium.FRAGMENT_REPORT_C -> {
                _result = _result.sortedBy { it.categoryCode }
                getString(R.string.quest_export_this_report_C)
            }
            Medium.FRAGMENT_REPORT_D -> {
                _result = _result.sortedBy { it.eventDate }
                getString(R.string.quest_export_this_report_D)
            }
        }
        val dialogExport = AlertDialog.Builder(requireContext())
        dialogExport.setIcon(R.mipmap.ic_mikan)
        dialogExport.setTitle(getString(R.string.export_category))
        dialogExport.setMessage(message)
        dialogExport.setPositiveButton(R.string.yes) { _, _ ->
            val runnable = Runnable {
                val stringBuilder = StringBuilder()
                stringBuilder.setLength(0)
                stringBuilder.append(resources.getString(R.string.category))
                stringBuilder.append(",")
                stringBuilder.append(resources.getString(R.string.amount))
                stringBuilder.append(",")
                stringBuilder.append(resources.getString(R.string.memo))
                stringBuilder.append(",")
                stringBuilder.append(resources.getString(R.string.event_date))
                stringBuilder.append(",")
                stringBuilder.append(resources.getString(R.string.updated_date))
                stringBuilder.append("\n")

                for (item in _result) {
                    stringBuilder.append(MainActivity.allCategoryMap[item.categoryCode]!!.name)
                    stringBuilder.append(",")
                    stringBuilder.append(item.getAmount())
                    stringBuilder.append(",")
                    stringBuilder.append(item.memo)
                    stringBuilder.append(",")
                    stringBuilder.append(item.eventDate)
                    stringBuilder.append(",")
                    stringBuilder.append(item.updateDate)
                    stringBuilder.append("\n")
                }

                UtilFiles.writeToFile(ExportActivity.FILE_ORDER_CATEGORY,
                        stringBuilder.toString(), requireActivity(), Context.MODE_PRIVATE)
            }
            val thread = Thread(runnable)
            thread.start()
            val intent = Intent(requireContext(), ExportActivity::class.java)
            intent.putExtra("REPORT_VIEW_TYPE", REPORT_BY_CATEGORY)
            startActivity(intent)
        }
        dialogExport.show()
    }

    /* Called by MainActivity */
    fun focusOnSavedItem(date: String) {
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
        _expandableList.clear()
        _expandableListAdapter.setMasterMap(masterMap)

        val allItemsByCategory = _result.groupBy { it.categoryCode }
                .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
        _incomeListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second > BigDecimal(0) })
        _expenseListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second < BigDecimal(0) })

        _medium.setInSearchResult(true)
    }

    /* exiting search */
    override fun onButtonClicked() {
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
            _expandableList.clear()
            _expandableListAdapter.setMasterMap(masterMap)

            val allItemsThisMonthByCategory = _itemsThisMonth.groupBy { it.categoryCode }
                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.getAmount()} ) }
            _incomeListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
            _expenseListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second < BigDecimal(0) })

            _medium.setInSearchResult(false)
            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_D)
            _reportC.visibility = GONE
            _reportD.visibility = VISIBLE
        }
        dialog.show()
    }
}