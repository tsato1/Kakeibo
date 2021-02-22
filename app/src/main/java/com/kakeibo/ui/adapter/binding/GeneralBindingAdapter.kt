package com.kakeibo.ui.adapter.binding

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.databinding.BannerDatePickerBinding
import com.kakeibo.databinding.FragmentReportCBinding
import com.kakeibo.ui.model.Medium
import com.kakeibo.util.UtilDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

@BindingAdapter("bind:context", "bind:place")
fun updateDateBannerYMD(view: View, context: Context, place: Int) {
    val binding = DataBindingUtil.getBinding<BannerDatePickerBinding>(view)

    binding?.let {
        val dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        val weekNames = context.resources.getStringArray(R.array.week_name)

        val cal = Calendar.getInstance()

        it.btnDate.text = when (place) {
            Medium.FRAGMENT_INPUT, Medium.REPORT_D -> UtilDate.getTodaysDateWithDay(dateFormat, weekNames)
            else -> UtilDate.getTodaysYM(dateFormat)
        }

        when (place) {
            Medium.FRAGMENT_INPUT, Medium.REPORT_D -> {
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
            }
            Medium.REPORT_C, Medium.REPORT_S -> {
                it.btnDate.setOnClickListener{}
            }
        }

        /* Next Button */
        it.btnNext.setOnClickListener { _ ->
            try {
                val date: Date
                val str = when (place) {
                    Medium.FRAGMENT_INPUT, Medium.REPORT_D -> {
                        cal.add(Calendar.DATE, 1)
                        date = cal.time
                        SimpleDateFormat(UtilDate.DATE_FORMATS[dateFormat],
                                Locale.getDefault()).format(date) + " [" + weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    Medium.REPORT_C -> {
                        cal.add(Calendar.MONTH, 1)
                        date = cal.time
                        val format = if (UtilDate.DATE_FORMATS[dateFormat]==UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
                        SimpleDateFormat(format, Locale.getDefault()).format(date)
                    }
                    else -> UtilDate.getTodaysDateWithDay(dateFormat, weekNames)
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
                val str = when (place) {
                    Medium.FRAGMENT_INPUT, Medium.REPORT_D -> {
                        cal.add(Calendar.DATE, -1)
                        date = cal.time
                        SimpleDateFormat(UtilDate.DATE_FORMATS[dateFormat],
                                Locale.getDefault()).format(date) +
                                " [" + weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    Medium.REPORT_C -> {
                        cal.add(Calendar.MONTH, -1)
                        date = cal.time
                        val format = if (UtilDate.DATE_FORMATS[dateFormat]==UtilDate.DATE_FORMAT_YMD) "yyyy/MM" else "MM/yyyy"
                        SimpleDateFormat(format, Locale.getDefault()).format(date)
                    }
                    else -> UtilDate.getTodaysDateWithDay(dateFormat, weekNames)
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
    val binding = DataBindingUtil.getBinding<FragmentReportCBinding>(view)

    binding?.let {
        it.horizontalBarChart.setDrawBarShadow(false)
        it.horizontalBarChart.setDrawValueAboveBar(true)
        it.horizontalBarChart.setDrawGridBackground(false)
        it.horizontalBarChart.setMaxVisibleValueCount(2) //income and expense
        it.horizontalBarChart.setFitBars(true)
        it.horizontalBarChart.isHighlightPerDragEnabled = false
        it.horizontalBarChart.isHighlightPerTapEnabled = false
        it.horizontalBarChart.isDoubleTapToZoomEnabled = false
        it.horizontalBarChart.setNoDataTextColor(R.color.colorBlack)
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

        it.pieGraphIncome.thickness = 120
        it.pieGraphExpense.thickness = 120
        ViewCompat.setNestedScrollingEnabled(it.pieGraphIncome, true)
        ViewCompat.setNestedScrollingEnabled(it.pieGraphExpense, true)
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
        expenseBarDataSet.setColors(context.resources.getColor(R.color.colorPrimary), context.resources.getColor(R.color.colorAccent))
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

//@BindingAdapter("bind:context", "bind:list")
//fun updateIncomePieGraph(graph: PieGraph?, context: Context?, list: Array<ItemStatus>) {
//    graph?.let {
//        it.removeSlices()
//        var inPieSlice: PieSlice
//
//        for (item in list) {
//            inPieSlice = PieSlice()
//            inPieSlice.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
//            inPieSlice.value = item.getAmount().toFloat()
//            it.addSlice(inPieSlice)
//        }
//    }
//}
//
//@BindingAdapter("bind:list")
//fun updateExpensePieGraph(graph: PieGraph?, list: LiveData<List<ItemStatus>>?) {
//    graph?.let { g ->
//        g.removeSlices()
//        var exPieSlice: PieSlice
//
//        val size = Constants.CATEGORY_COLORS.size
//        list?.value?.let {
//            for ((i, item) in it.withIndex()) {
//                exPieSlice = PieSlice()
//                exPieSlice.color =
//                        if (i < size) Constants.CATEGORY_COLORS[i].toColorInt()
//                        else Constants.CATEGORY_COLORS[size - 1].toColorInt()
//                exPieSlice.value = item.getAmount().toFloat()
//                g.addSlice(exPieSlice)
//                Log.d("asdf", exPieSlice.value.toString())
//            }
//        }
//    }
//}