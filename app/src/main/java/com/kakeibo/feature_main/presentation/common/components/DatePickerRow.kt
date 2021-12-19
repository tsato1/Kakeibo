package com.kakeibo.feature_main.presentation.common.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.DateHandleViewModel
import com.kakeibo.util.UtilDate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerRow(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: DateHandleViewModel,
    type: DateType
) {
    val weekNames = stringArrayResource(id = R.array.week_name)
    val dateFormatIndex = viewModel.appPreferences.getDateFormatIndex()

    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val date = when (type) {
        is DateType.YMDW -> {
            remember {
                mutableStateOf(
                    UtilDate.getTodaysYMD(UtilDate.DATE_FORMATS[dateFormatIndex]) +
                            " [" + weekNames[calendar[Calendar.DAY_OF_WEEK] - 1] + "]"
                )
            }
        }
        is DateType.YM -> {
            remember {
                mutableStateOf(UtilDate.getTodaysYM(UtilDate.DATE_FORMATS[dateFormatIndex]))
            }
        }
        is DateType.Y -> {
            remember {
                mutableStateOf(UtilDate.getTodaysY())
            }
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            calendar.set(y, m, d)
            when (type) {
                is DateType.YMDW -> {
                    date.value = SimpleDateFormat(
                        UtilDate.DATE_FORMATS[dateFormatIndex],
                        Locale.getDefault()
                    ).format(calendar.time) + " [" + weekNames[calendar[Calendar.DAY_OF_WEEK] - 1] + "]"
                }
                is DateType.YM -> {
                    date.value = SimpleDateFormat(
                        "yyyy/MM",
                        Locale.getDefault()
                    ).format(calendar.time)
                }
                is DateType.Y -> {
                    date.value = SimpleDateFormat(
                        "yyyy",
                        Locale.getDefault()
                    ).format(calendar.time)
                }
            }
        }, year, month, day
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                date.value = when (type) {
                    is DateType.YMDW -> {
                        calendar.add(Calendar.DATE, -1)
                        SimpleDateFormat(
                            UtilDate.DATE_FORMATS[dateFormatIndex],
                            Locale.getDefault()
                        ).format(calendar.time) + " [" + weekNames[calendar[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    is DateType.YM -> {
                        calendar.add(Calendar.MONTH, -1)
                        SimpleDateFormat(
                            "yyyy/MM",
                            Locale.getDefault()
                        ).format(calendar.time)
                    }
                    is DateType.Y -> {
                        calendar.add(Calendar.YEAR, -1)
                        SimpleDateFormat(
                            "yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_left_24),
                contentDescription = "Previous"
            )
        }
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = {
                datePickerDialog.show()
            }
        ) {
            Text(
                text = date.value,
                onTextLayout = {
                    viewModel.onDateChanged(date.value)
                }
            )
        }
        IconButton(
            onClick = {
                date.value = when (type) {
                    is DateType.YMDW -> {
                        calendar.add(Calendar.DATE, 1)
                        SimpleDateFormat(
                            UtilDate.DATE_FORMATS[dateFormatIndex],
                            Locale.getDefault()
                        ).format(calendar.time) + " [" + weekNames[calendar[Calendar.DAY_OF_WEEK] - 1] + "]"
                    }
                    is DateType.YM -> {
                        calendar.add(Calendar.MONTH, 1)
                        SimpleDateFormat(
                            "yyyy/MM",
                            Locale.getDefault()
                        ).format(calendar.time)
                    }
                    is DateType.Y -> {
                        calendar.add(Calendar.YEAR, 1)
                        SimpleDateFormat(
                            "yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_right_24),
                contentDescription = "Next"
            )
        }
    }
}

sealed class DateType {
    object YMDW: DateType()
    object YM: DateType()
    object Y: DateType()
}
