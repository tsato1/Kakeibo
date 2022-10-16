package com.kakeibo.feature_main.presentation.common.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.DateViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.of
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilDate.toYMDWString
import com.kakeibo.util.UtilDate.toYMString
import java.util.*

@Composable
fun DatePickerRow(
    modifier: Modifier = Modifier,
    context: Context,
    type: DateType, // depending on which screen, date text on the button differs
    dateFormatIndex: Int, // date format that's stored in shared pref
    viewModel: DateViewModel
) {
    val cal by viewModel.cal.collectAsState()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            val date = Calendar.getInstance()
                .of(y, m, d)
                .toYMDString(UtilDate.DATE_FORMAT_DB)
            viewModel.updateLocalEventDate(date)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                when (type) {
                    is DateType.YMDW -> { viewModel.plus(Calendar.DAY_OF_MONTH, -1) }
                    is DateType.YM -> { viewModel.plus(Calendar.MONTH, -1) }
                    is DateType.Y -> { viewModel.plus(Calendar.YEAR, -1) }
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
                text =  when (type) {
                    is DateType.YMDW -> {
                        cal.toYMDWString(UtilDate.DATE_FORMATS[dateFormatIndex], context)
                    }
                    is DateType.YM -> {
                        cal.toYMString(UtilDate.DATE_FORMATS[dateFormatIndex])
                    }
                    is DateType.Y -> {
                        cal.get(Calendar.YEAR).toString()
                    }
                }
            )
        }
        IconButton(onClick = { viewModel.resetToToday() }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
        }
        IconButton(
            onClick = {
                when (type) {
                    is DateType.YMDW -> { viewModel.plus(Calendar.DAY_OF_MONTH, 1) }
                    is DateType.YM -> { viewModel.plus(Calendar.MONTH, 1) }
                    is DateType.Y -> { viewModel.plus(Calendar.YEAR, 1) }
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
