//package com.kakeibo.ui
//
//import android.content.pm.ActivityInfo
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import androidx.recyclerview.widget.ConcatAdapter
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.kakeibo.R
//import com.kakeibo.ui.adapter.view.ExpandableItem
//import com.kakeibo.ui.adapter.view.ExpandableListAdapter
//import com.kakeibo.feature_settings.settings_category.presentation.CategoryViewModel
//import com.kakeibo.feature_item.presentation.item_list.ItemViewModel
//import dagger.hilt.android.AndroidEntryPoint
//
///**
// * Created by T on 2015/09/14.
// */
//@AndroidEntryPoint
//class ReportFragment : Fragment(R.layout.fragment_report) {
//
////    companion object {
////        private const val SWIPE_REFRESH_MILLI_SECOND = 200
////
////        fun newInstance(): ReportFragment {
////            val tabFragment2 = ReportFragment()
////            val args = Bundle()
////            tabFragment2.arguments = args
////            return tabFragment2
////        }
////
////        private var dateToFocus: String = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB)
////    }
//
//    private val itemViewModel: ItemViewModel by viewModels()
//    private val categoryViewModel: CategoryViewModel by viewModels()
//
//    private val args: ReportFragmentArgs by navArgs()
//
//    private lateinit var expandableItemsAdapter: ExpandableListAdapter
//    private var expandableItemList = listOf<ExpandableItem>()
//
////    private val _medium: Medium by activityViewModels()
////
////    private var currentlyShown: Int = Medium.FRAGMENT_REPORT_DATE_MONTHLY
////
////    private lateinit var _srlReload: SwipeRefreshLayout
////    private lateinit var _btnClose: ImageButton
////    private lateinit var _reportCategoryYearly: LinearLayout
////    private lateinit var _reportDateYearly: LinearLayout
////    private lateinit var _reportCategoryMonthly: LinearLayout
////    private lateinit var _reportDateMonthly: LinearLayout
////    private lateinit var _btnDate: Button
////    private lateinit var _btnNext: ImageButton
////    private lateinit var _btnPrev: ImageButton
////    private lateinit var _cal: Calendar // todo use kotlin calendar
////
////    private lateinit var _allItems: List<Item>
////    private lateinit var _itemsThisMonth: List<Item>
////    private lateinit var _allCategoriesMap: Map<Int, Category>
////    private var _result: List<Item> = listOf()
////    private lateinit var _expandableListAdapter: ExpandableListAdapter
////    private lateinit var _incomeListAdapter: ReportCListAdapter
////    private lateinit var _expenseListAdapter: ReportCListAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        setHasOptionsMenu(true)
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        requireActivity().requestedOrientation =
//            ActivityInfo.SCREEN_ORIENTATION_USER // the orientation user chose
//
//        subscribeToObservers()
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.rcv_items)
//        val arrayList = arrayListOf<ExpandableListAdapter>()
////        val expandableItemList = getGroupedItemList(50)
//
//        for (groupedItem in expandableItemList) {
//            val adapter = ExpandableListAdapter(groupedItem)
//            arrayList.add(adapter)
//        }
//
//        val concatAdapterConfig = ConcatAdapter.Config.Builder()
//            .setIsolateViewTypes(false)
//            .build()
//
//        val concatAdapter = ConcatAdapter(concatAdapterConfig, arrayList)
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = concatAdapter
//
//        val fabInput = view.findViewById<FloatingActionButton>(R.id.fab_input)
//        fabInput.setOnClickListener {
//            findNavController().navigate(ReportFragmentDirections.actionReportFragmentToInputFragment())
//        }
//    }
//
//    private fun subscribeToObservers() {
//        itemViewModel.expandableItems.observe(viewLifecycleOwner, {
//            it?.let { items ->
//                expandableItemList = items
//            }
//        })
//    }
//
////    fun getGroupedItemList(max: Int): ArrayList<ExpandableItem> {
////        val groupedItemList = arrayListOf<ExpandableItem>()
////        for (i in 0 until max) {
////            val headerText = "Header $i"
////            val header = ExpandableItem.Parent(headerText)
////
////            val itemList = arrayListOf<ExpandableItem.Child>()
////            val indexNum = Random.nextInt(2, 5)
////            for (j in 0 until indexNum) {
////                val itemText = "Item $j"
////                itemList.add(
////                    ExpandableItem.Child(
////                        Item(
////                            BigDecimal(10), itemText, j, "memo", "eventDate", "updateDate"
////                        )
////                    )
////                )
////            }
////            val groupedItem = ExpandableItem(header, itemList)
////            groupedItemList.add(groupedItem)
////        }
////        return groupedItemList
////    }
//
////    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
////        super.onCreateView(inflater, container, savedInstanceState)
//
////        val fragmentBinding = FragmentReportBinding.inflate(inflater, container, false)
////        fragmentBinding.lifecycleOwner = this
////        fragmentBinding.itemViewModel = _itemViewModel
////        val view = fragmentBinding.root
////
////        val reportCategoryMonthlyBinding = fragmentBinding.fragmentReportCategoryMonthly
////        reportCategoryMonthlyBinding.lifecycleOwner = this
////        reportCategoryMonthlyBinding.itemStatusViewModel = _itemViewModel
////        reportCategoryMonthlyBinding.categoryStatusViewModel = _categoryViewModel
////
////        val reportCategoryYearlyBinding = fragmentBinding.fragmentReportCategoryYearly
////        reportCategoryYearlyBinding.lifecycleOwner = this
////        reportCategoryYearlyBinding.itemStatusViewModel = _itemViewModel
////        reportCategoryYearlyBinding.categoryStatusViewModel = _categoryViewModel
////
////        val bannerDatePickerBinding = fragmentBinding.bannerDatePicker
////        bannerDatePickerBinding.lifecycleOwner = this
////        bannerDatePickerBinding.medium = _medium
////        bannerDatePickerBinding.clickListener = this
////        bannerDatePickerBinding.itemViewModel = _itemViewModel
////
////        val expandableListView: RecyclerView = view.findViewById(R.id.lsv_expandable)
////        _expandableListAdapter = ExpandableListAdapter(_itemViewModel, _categoryViewModel, this)
////        expandableListView.adapter = _expandableListAdapter
////        expandableListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
////
////        val incomeListView: RecyclerView = view.findViewById(R.id.rcv_income)
////        _incomeListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_INCOME, _categoryViewModel, this)
////        incomeListView.adapter = _incomeListAdapter
////        val expenseListView: RecyclerView = view.findViewById(R.id.rcv_expense)
////        _expenseListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_EXPENSE, _categoryViewModel, this)
////        expenseListView.adapter = _expenseListAdapter
////
////        _itemViewModel.all.observe(viewLifecycleOwner, {
////            _allItems = it
////        })
////        _categoryViewModel.allMap.observe(viewLifecycleOwner, {
////            _allCategoriesMap = it
////        })
////        _itemViewModel.items.observe(viewLifecycleOwner, { allThisMonth ->
////            _itemsThisMonth = allThisMonth
////
////            val masterMap = allThisMonth
////                    .groupBy { it.eventDate }
////                    .mapKeys { entry ->
////                        ExpandableListRowModel.Header(
////                                entry.key,
////                                entry.value.asSequence().filter{ it.amount > BigDecimal(0) }.sumOf { it.amount },
////                                entry.value.asSequence().filter{ it.amount < BigDecimal(0) }.sumOf { it.amount }
////                        ) }
////                    .toSortedMap(compareBy { it.date })
////            _expandableListAdapter.setData(masterMap, dateToFocus)
////
////            val allThisMonthByCategory = allThisMonth
////                    .groupBy { it.categoryCode }
////                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.amount} ) }
////            _incomeListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
////            _expenseListAdapter.setAllByCategory(allThisMonthByCategory.filter { it.key.second < BigDecimal(0) })
////        })
////
////        _medium.currentlyShownLive.observe(viewLifecycleOwner, {
////            currentlyShown = it
////        })
////
////
////
////
////
////        _cal = Calendar.getInstance()
////        _btnDate = view.findViewById(R.id.btn_date)
////        _btnNext = view.findViewById(R.id.btn_next)
////        _btnPrev = view.findViewById(R.id.btn_prev)
////        _btnNext.setOnClickListener(ButtonClickListener())
////        _btnPrev.setOnClickListener(ButtonClickListener())
////        _btnDate.setOnClickListener {}
////
////        _btnDate.text = UtilDate.getTodaysYM(dateFormat)
////        _btnDate.setOnClickListener {}
////        _btnNext.visibility = View.VISIBLE
////        _btnPrev.visibility = View.VISIBLE
////
////
////        val btnA: Button = view.findViewById(R.id.btn_category_yearly)
////        val btnB: Button = view.findViewById(R.id.btn_date_yearly)
////        val btnC: Button = view.findViewById(R.id.btn_category_monthly)
////        val btnD: Button = view.findViewById(R.id.btn_date_monthly)
////        _reportCategoryYearly = view.findViewById(R.id.fragment_report_category_yearly)
////        _reportDateYearly = view.findViewById(R.id.fragment_report_date_yearly)
////        _reportCategoryMonthly = view.findViewById(R.id.fragment_report_category_monthly)
////        _reportDateMonthly = view.findViewById(R.id.fragment_report_date_monthly)
////        btnA.setOnClickListener { v ->
////            _reportCategoryYearly.visibility = VISIBLE
////            _reportDateYearly.visibility = GONE
////            _reportCategoryMonthly.visibility = GONE
////            _reportDateMonthly.visibility = GONE
////            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_YEARLY)
////            bannerDatePickerBinding.executePendingBindings()
////        }
////        btnB.setOnClickListener { v ->
////            _reportCategoryYearly.visibility = GONE
////            _reportDateYearly.visibility = VISIBLE
////            _reportCategoryMonthly.visibility = GONE
////            _reportDateMonthly.visibility = GONE
////            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_YEARLY)
////            bannerDatePickerBinding.executePendingBindings()
////        }
////        btnC.setOnClickListener { v ->
////            _reportCategoryYearly.visibility = GONE
////            _reportDateYearly.visibility = GONE
////            _reportCategoryMonthly.visibility = VISIBLE
////            _reportDateMonthly.visibility = GONE
////            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY)
////            bannerDatePickerBinding.executePendingBindings()
////        }
////        btnD.setOnClickListener { v ->
////            _reportCategoryYearly.visibility = GONE
////            _reportDateYearly.visibility = GONE
////            _reportCategoryMonthly.visibility = GONE
////            _reportDateMonthly.visibility = VISIBLE
////            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
////            bannerDatePickerBinding.executePendingBindings()
////        }
////
////        _srlReload = view.findViewById(R.id.srl_reload)
////        _btnClose = view.findViewById(R.id.btn_exit_search_result)
////        _srlReload.setOnRefreshListener {
////            Handler(Looper.getMainLooper()).postDelayed({
////                _srlReload.isRefreshing = false
////            }, SWIPE_REFRESH_MILLI_SECOND.toLong())
////        }
////
////        return view
////    }
//
//    /* Called by MainActivity */
////    fun export() {
////        val dialogExport = AlertDialog.Builder(requireContext())
////        dialogExport.setIcon(R.mipmap.ic_mikan)
////        dialogExport.setTitle(getString(R.string.export_category))
////        dialogExport.setMessage(when (currentlyShown) {
////            Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY ->
////                R.string.quest_export_this_report_C
////            Medium.FRAGMENT_REPORT_DATE_MONTHLY ->
////                R.string.quest_export_this_report_D
////            else -> {
////                Toast.makeText(requireContext(), "Something is wrong. Cannot export.", Toast.LENGTH_SHORT).show()
////                return
////            }
////        })
////        dialogExport.setPositiveButton(R.string.yes) { _, _ ->
////            val fileName = when (currentlyShown) {
////                Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY ->
////                    ImportExportActivity.ExportType.FILE_ORDER_CATEGORY
////                Medium.FRAGMENT_REPORT_DATE_MONTHLY ->
////                    ImportExportActivity.ExportType.FILE_ORDER_DATE
////                else -> ""
////            }
////
////            val itemsToExport = if (_medium.getInSearchResult()) {
////                _result
////            } else {
////                _itemsThisMonth
////            }
////
////            when (currentlyShown) {
////                Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY -> {
////                    UtilExport.buildOrderByCategory(
////                        requireContext(), itemsToExport, _allCategoriesMap, fileName)
////                }
////                Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
////                    UtilExport.buildOrderByDate(
////                        requireContext(), itemsToExport, _allCategoriesMap, fileName)
////                }
////            }
////            val intent = Intent(requireContext(), ImportExportActivity::class.java)
////            intent.putExtra("ORDER_TYPE", ImportExportActivity.OrderType.EXPORT)
////            intent.putExtra("EXPORT_TYPE", currentlyShown)
////            startActivity(intent)
////        }
////        dialogExport.show()
////    }
////
////    /* Called by MainActivity */
////    fun focusOnSavedItem(date: String) {
////        _reportCategoryYearly.visibility = GONE
////        _reportDateYearly.visibility = GONE
////        _reportCategoryMonthly.visibility = GONE
////        _reportDateMonthly.visibility = VISIBLE
////        dateToFocus = date
////        _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
////    }
////
////    /* Called by MainActivity */
////    fun onSearch(query: Query) {
////        _btnClose.visibility = VISIBLE
////        _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_background_search))
////
////        _result = UtilQuery.query(_allItems, query)
////
////        _itemViewModel.setMutableAll(_result)
////        val masterMap = _result
////                .groupBy { it.eventDate }
////                .mapKeys { entry ->
////                    ExpandableListRowModel.Header(
////                            entry.key,
////                            entry.value.asSequence().filter{ it.amount > BigDecimal(0) }.sumOf { it.amount },
////                            entry.value.asSequence().filter{ it.amount < BigDecimal(0) }.sumOf { it.amount }) }
////                .toSortedMap(compareBy { it.date })
////        _expandableListAdapter.setData(masterMap, dateToFocus)
////
////        val allItemsByCategory = _result
////                .groupBy { it.categoryCode }
////                .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.amount} ) }
////        _incomeListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second > BigDecimal(0) })
////        _expenseListAdapter.setAllByCategory(allItemsByCategory.filter { it.key.second < BigDecimal(0) })
////
////        _medium.setInSearchResult(true)
////
////
////
////
////        _btnDate.text = getString(R.string.search_result)
////        _btnNext.visibility = View.GONE
////        _btnPrev.visibility = View.GONE
////    }
////
////    /* exiting search */
////    override fun onClick() {
////        val dialog = AlertDialog.Builder(requireContext())
////        dialog.setIcon(R.mipmap.ic_mikan)
////        dialog.setTitle(getString(R.string.returning_to_monthly_report))
////        dialog.setMessage(getString(R.string.msg_exit_search))
////        dialog.setPositiveButton(R.string.ok) { _, _ ->
////            _btnClose.visibility = GONE
////            _srlReload.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_background))
////
////            _itemViewModel.setItemsThisMonth()
////
////            val masterMap = _itemsThisMonth
////                    .groupBy { it.eventDate }
////                    .mapKeys { entry ->
////                        ExpandableListRowModel.Header(
////                                entry.key,
////                                entry.value.asSequence().filter{ it.amount > BigDecimal(0) }.sumOf { it.amount },
////                                entry.value.asSequence().filter{ it.amount < BigDecimal(0) }.sumOf { it.amount }) }
////                    .toSortedMap(compareBy { it.date })
////            _expandableListAdapter.setData(masterMap, dateToFocus)
////
////            val allItemsThisMonthByCategory = _itemsThisMonth
////                    .groupBy { it.categoryCode }
////                    .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.amount} ) }
////            _incomeListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second > BigDecimal(0) })
////            _expenseListAdapter.setAllByCategory(allItemsThisMonthByCategory.filter { it.key.second < BigDecimal(0) })
////
////            _medium.setInSearchResult(false)
////            _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT_DATE_MONTHLY)
////            _reportCategoryYearly.visibility = GONE
////            _reportDateYearly.visibility = GONE
////            _reportCategoryMonthly.visibility = GONE
////            _reportDateMonthly.visibility = VISIBLE
////        }
////        dialog.show()
////
////
////
////
////        _btnDate.text = UtilDate.getTodaysYM(dateFormat)
////        _btnNext.visibility = View.VISIBLE
////        _btnPrev.visibility = View.VISIBLE
////    }
//
//
//
//
//
//
//
//
//
////    internal inner class ButtonClickListener : View.OnClickListener {
////        override fun onClick(v: View) {
////            when (v.id) {
////                R.id.btn_next -> {
////                    try {
////                        _cal.add(Calendar.MONTH, 1)
////                        val date: Date = _cal.time
////                        val format = if (UtilDate.DATE_FORMATS[dateFormat] == UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
////                        val out = SimpleDateFormat(format, Locale.getDefault()).format(date)
////                        val dbDate = SimpleDateFormat(UtilDate.DATE_FORMAT_DB, Locale.getDefault()).format(date)
////                        _itemViewModel.setItemsYM(dbDate.split("-")[0], dbDate.split("-")[1])
////                        _btnDate.text = out
////                    } catch (e: ParseException) {
////                        e.printStackTrace()
////                    }
////                }
////                R.id.btn_prev -> {
////                    try {
////                        _cal.add(Calendar.MONTH, -1)
////                        val date: Date = _cal.time
////                        val format = if (UtilDate.DATE_FORMATS[dateFormat] == UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
////                        val out = SimpleDateFormat(format, Locale.getDefault()).format(date)
////                        val dbDate = SimpleDateFormat(UtilDate.DATE_FORMAT_DB, Locale.getDefault()).format(date)
////                        _itemViewModel.setItemsYM(dbDate.split("-")[0], dbDate.split("-")[1])
////                        _btnDate.text = out
////                    } catch (e: ParseException) {
////                        e.printStackTrace()
////                    }
////                }
////            }
////        }
////    }
//}