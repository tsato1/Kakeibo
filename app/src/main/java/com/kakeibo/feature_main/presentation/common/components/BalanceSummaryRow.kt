package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_main.item_chart.ItemChartState
import com.kakeibo.util.UtilCategory
import java.math.BigDecimal

@Composable
fun BalanceSummaryRow(
    itemChartState: State<ItemChartState>
) {
    // Income
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp, 8.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_INCOME)
        Text(
            text = stringResource(id = R.string.income_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append("+")
                }
                append(itemChartState.value.incomeTotal)
            }
        )
    }
    // Expense
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp, 8.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_EXPENSE)
        Text(
            text = stringResource(id = R.string.expense_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)) {
                    append("-")
                }
                append(itemChartState.value.expenseTotal)
            }
        )
    }
    // Balance
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 0.dp, 8.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_NONE)
        Text(
            text = stringResource(id = R.string.balance_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        val balance = (itemChartState.value.incomeTotal.toBigDecimal() -
                itemChartState.value.expenseTotal.toBigDecimal())
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = if (balance < BigDecimal.ZERO) {
                            Color.Red
                        }
                        else if (balance > BigDecimal.ZERO) {
                            Color.Blue
                        }
                        else {
                            Color.Black
                        }
                    )
                ) {
                    if (balance < BigDecimal.ZERO) {
                        append("-")
                    }
                    else if (balance > BigDecimal.ZERO) {
                        append("+")
                    }

                    append(balance.toString())
                }
            }
        )
    }
}