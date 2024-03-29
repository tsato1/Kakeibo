package com.kakeibo.feature_main.presentation.item_search.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_search.ItemSearchEvent
import com.kakeibo.feature_main.presentation.item_search.ItemSearchViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.of
import com.kakeibo.util.UtilDate.toYMDString
import java.util.*

@Composable
fun SearchCardDateRange(
    modifier: Modifier = Modifier,
    viewModel: ItemSearchViewModel
) {
    val context = LocalContext.current

    val dateFormatIndex = viewModel.dateFormatIndex
    val dateState = viewModel.searchCardDateRangeState.value

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.date_range))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val todaysDate = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _: DatePicker, y: Int, m: Int, d: Int ->
                            viewModel.onEvent(
                                ItemSearchEvent.DateFromSelected(Calendar.getInstance().of(y, m, d))
                            )
                        },
                        todaysDate.get(Calendar.YEAR),
                        todaysDate.get(Calendar.MONTH),
                        todaysDate.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Text(text = dateState.from.toYMDString(UtilDate.DATE_FORMATS[dateFormatIndex]))
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "-"
            )
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val todaysDate = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _: DatePicker, y: Int, m: Int, d: Int ->
                            viewModel.onEvent(
                                ItemSearchEvent.DateToSelected(Calendar.getInstance().of(y, m, d))
                            )
                        },
                        todaysDate.get(Calendar.YEAR),
                        todaysDate.get(Calendar.MONTH),
                        todaysDate.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Text(text = dateState.to.toYMDString(UtilDate.DATE_FORMATS[dateFormatIndex]))
            }
        }
    }
}