package com.kakeibo.ui.adapter.binding

import android.app.DatePickerDialog
import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.Category
import com.kakeibo.databinding.BannerDatePickerBinding
import com.kakeibo.databinding.FragmentReportCategoryMonthlyBinding
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.viewmodel.ItemViewModel
import com.kakeibo.util.UtilDate
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

@BindingAdapter("bind:context", "bind:currentlyShown", "bind:inSearch", "bind:itemViewModel")
fun updateDateBannerYMD(view: View,
                        context: Context,
                        currentlyShown: Int,
                        inSearch: Boolean,
                        itemViewModel: ItemViewModel?) {
    val binding = DataBindingUtil.getBinding<BannerDatePickerBinding>(view)

    binding?.let {
        if (inSearch) {
            it.btnDate.setOnClickListener{}
            it.btnDate.text = context.getString(R.string.search_result)
            it.btnNext.visibility = View.GONE
            it.btnPrev.visibility = View.GONE
            return
        }

        val dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        val weekNames = context.resources.getStringArray(R.array.week_name)
        val cal = Calendar.getInstance()

        when (currentlyShown) {
            Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY, Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
                it.btnDate.text = UtilDate.getTodaysYM(dateFormat)
                it.btnDate.setOnClickListener {}
                it.btnNext.visibility = View.VISIBLE
                it.btnPrev.visibility = View.VISIBLE
            }
            Medium.FRAGMENT_INPUT -> {
                it.btnDate.text = UtilDate.getTodaysDateWithDay(dateFormat, weekNames)
                it.btnDate.setOnClickListener { _ ->
                    val year = cal[Calendar.YEAR]
                    val month = cal[Calendar.MONTH] + 1
                    val day = cal[Calendar.DAY_OF_MONTH]
                    val dialog = DatePickerDialog(context, { _, y, m, d ->
                        val gCal = GregorianCalendar(y, m, d)
                        val str = SimpleDateFormat(UtilDate.DATE_FORMATS[dateFormat],
                                Locale.getDefault()).format(gCal.time) +
                                " [" + weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                        it.btnDate.text = str
                    }, year, month - 1, day)
                    dialog.show()
                }
                it.btnNext.visibility = View.VISIBLE
                it.btnPrev.visibility = View.VISIBLE
            }
        }

        /* Next Button */
        it.btnNext.setOnClickListener { _ ->
            try {
                val date: Date
                val str = when (currentlyShown) {
                    Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY, Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
                        cal.add(Calendar.MONTH, 1)
                        date = cal.time
                        val format = if (UtilDate.DATE_FORMATS[dateFormat]==UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
                        val out = SimpleDateFormat(format, Locale.getDefault()).format(date)
                        val dbDate = SimpleDateFormat(UtilDate.DATE_FORMAT_DB, Locale.getDefault()).format(date)
                        itemViewModel?.setItemsYM(dbDate.split("-")[0], dbDate.split("-")[1])
                        out
                    }
                    Medium.FRAGMENT_INPUT -> {
                        cal.add(Calendar.DATE, 1)
                        date = cal.time
                        SimpleDateFormat(UtilDate.DATE_FORMATS[dateFormat],
                                Locale.getDefault()).format(date) +
                                " [" + weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    else -> ""
                }
                it.btnDate.text = str
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        /* Prev Button */
        it.btnPrev.setOnClickListener { _ ->
            try {
                val date: Date
                val str = when (currentlyShown) {
                    Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY, Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
                        cal.add(Calendar.MONTH, -1)
                        date = cal.time
                        val format = if (UtilDate.DATE_FORMATS[dateFormat]==UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
                        val out = SimpleDateFormat(format, Locale.getDefault()).format(date)
                        val dbDate = SimpleDateFormat(UtilDate.DATE_FORMAT_DB, Locale.getDefault()).format(date)
                        itemViewModel?.setItemsYM(dbDate.split("-")[0], dbDate.split("-")[1])
                        out
                    }
                    Medium.FRAGMENT_INPUT -> {
                        cal.add(Calendar.DATE, -1)
                        date = cal.time
                        SimpleDateFormat(UtilDate.DATE_FORMATS[dateFormat],
                                Locale.getDefault()).format(date) +
                                " [" + weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    else -> ""
                }
                it.btnDate.text = str
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }
}

@BindingAdapter("bind:context")
fun prepareReportCView(view:View, context: Context) {
    val binding = DataBindingUtil.getBinding<FragmentReportCategoryMonthlyBinding>(view)

    binding?.let {
        it.horizontalBarChart.setDrawBarShadow(false)
        it.horizontalBarChart.setDrawValueAboveBar(true)
        it.horizontalBarChart.setDrawGridBackground(false)
        it.horizontalBarChart.setMaxVisibleValueCount(2) //income and expense
        it.horizontalBarChart.setFitBars(true)
        it.horizontalBarChart.isHighlightPerDragEnabled = false
        it.horizontalBarChart.isHighlightPerTapEnabled = false
        it.horizontalBarChart.isDoubleTapToZoomEnabled = false
        it.horizontalBarChart.setNoDataTextColor(R.color.color_black)
        it.horizontalBarChart.setPinchZoom(false)
        it.horizontalBarChart.description.isEnabled = false
//        it.horizontalBarChart.setOnChartValueSelectedListener(OnChartValueSelectedListener)
        it.horizontalBarChart.animateY(context.resources.getInteger(R.integer.chart_animation_milli_seconds))
        val xl = it.horizontalBarChart.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.granularity = 10f
        xl.isEnabled = false
        val yl = it.horizontalBarChart.axisLeft
        yl.setDrawAxisLine(true)
        yl.setDrawGridLines(true)
        yl.axisMinimum = 0f // this replaces setStartAtZero(true)
        val yr = it.horizontalBarChart.axisRight
        yr.setDrawAxisLine(true)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f // this replaces setStartAtZero(true)

        val legend = it.horizontalBarChart.legend
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        legend.setDrawInside(false);
//        legend.setFormSize(8f);
//        legend.setXEntrySpace(5f);
        legend.isEnabled = false
    }
}

@BindingAdapter("bind:context", "bind:income", "bind:expense")
fun updateBarChart(horizontalBarChart: HorizontalBarChart, context: Context, income: String?, expense: String?) {
    val outValue = TypedValue()
    context.resources.getValue(R.dimen.horizontal_bar_bar_width, outValue, true)

    val barWidth = outValue.float
    context.resources.getValue(R.dimen.horizontal_bar_bar_space, outValue, true)

    val spaceForBar = outValue.float
    val values = ArrayList<BarEntry>()

    income?.let { it -> values.add(BarEntry(0 * spaceForBar, it.toFloat())) }
    expense?.let { it -> values.add(BarEntry(1 * spaceForBar, it.toFloat().absoluteValue)) }

    val incomeBarDataSet: BarDataSet
    val expenseBarDataSet: BarDataSet

    if (horizontalBarChart.data != null && horizontalBarChart.data.dataSetCount > 0) {
        horizontalBarChart.invalidate()
        incomeBarDataSet = horizontalBarChart.data.getDataSetByIndex(0) as BarDataSet
        incomeBarDataSet.values = values
        expenseBarDataSet = horizontalBarChart.data.getDataSetByIndex(1) as BarDataSet
        expenseBarDataSet.values = values
        horizontalBarChart.data.notifyDataChanged()
        horizontalBarChart.notifyDataSetChanged()
    } else {
        incomeBarDataSet = BarDataSet(values, context.getString(R.string.income_colon))
        expenseBarDataSet = BarDataSet(values, context.getString(R.string.expense_colon))
        incomeBarDataSet.setDrawIcons(false)
        expenseBarDataSet.setDrawIcons(false)
        expenseBarDataSet.setColors(ContextCompat.getColor(context, R.color.color_primary), ContextCompat.getColor(context, R.color.color_accent))
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(incomeBarDataSet)
        dataSets.add(expenseBarDataSet)
        val data = BarData(dataSets)
        context.resources.getValue(R.dimen.horizontal_bar_text_size, outValue, true)
        data.setValueTextSize(outValue.float)
        data.barWidth = barWidth
        horizontalBarChart.data = data
    }
}

@BindingAdapter("bind:incomeList")
fun updateIncomePieGraph(pieGraph: PieGraph, incomeList: List<Pair<Int, BigDecimal>>?) {
    val size = Constants.CATEGORY_EXPENSE_COLORS.size

    incomeList?.let {
        pieGraph.removeSlices()
        var inPieSlice: PieSlice

        for ((i, item) in incomeList.withIndex()) {
            inPieSlice = PieSlice()
            inPieSlice.color =
                    if (i < size) Constants.CATEGORY_INCOME_COLORS[i].toColorInt()
                    else Constants.CATEGORY_INCOME_COLORS[size - 1].toColorInt()
            inPieSlice.value = item.second.toFloat()
            pieGraph.addSlice(inPieSlice)
        }
    }
//    incomeList?.let {
//        masterMap?.let {
//            val arr = incomeList.map { arrayOf(masterMap[it.first]!!.name, it.second.toInt()) }.toTypedArray()
//            val aaChartModel = AAChartModel()
//                    .chartType(AAChartType.Pie)
////            .title("title")
////            .subtitle("subtitle")
////            .backgroundColor("")
//                    .colorsTheme(Constants.CATEGORY_INCOME_COLORS as Array<Any>)
//                    .dataLabelsEnabled(true)
//                    .series(arrayOf(AASeriesElement().name(context.getString(R.string.income)).data(arr as Array<Any>)))
//
//            pieGraph.aa_drawChartWithChartModel(aaChartModel)
//        }
//    }
}

@BindingAdapter("bind:expenseList"/*, "bind:context", "bind:masterMap"*/)
fun updateExpensePieGraph(pieGraph: PieGraph, expenseList: List<Pair<Int, BigDecimal>>?/*, context: Context, masterMap: Map<Int, Category>?*/) {
    val size = Constants.CATEGORY_EXPENSE_COLORS.size

    expenseList?.let {
        pieGraph.removeSlices()
        var exPieSlice: PieSlice

        for ((i, item) in expenseList.withIndex()) {
            exPieSlice = PieSlice()
            exPieSlice.color =
                    if (i < size) Constants.CATEGORY_EXPENSE_COLORS[i].toColorInt()
                    else Constants.CATEGORY_EXPENSE_COLORS[size - 1].toColorInt()
            exPieSlice.value = item.second.toFloat()
            pieGraph.addSlice(exPieSlice)
        }
    }
//    expenseList?.let {
//        masterMap?.let {
//            val arr = expenseList.map { arrayOf(masterMap[it.first]!!.name, it.second.toInt()) }.toTypedArray()
//
//            val aaChartModel = AAChartModel()
//                    .chartType(AAChartType.Pie)
////            .title("title")
////            .subtitle("subtitle")
////            .backgroundColor("")
//                    .colorsTheme(Constants.CATEGORY_EXPENSE_COLORS as Array<Any>)
//                    .dataLabelsEnabled(true)
//                    .series(arrayOf(AASeriesElement().name(context.getString(R.string.expense)).data(arr as Array<Any>)))
//
//            pieGraph.aa_drawChartWithChartModel(aaChartModel)
//        }
//    }
}