package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_INCOME)
        Text(
            text = stringResource(id = R.string.income_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "+",
                style = TextStyle(
                    color = Color.Blue,
                    fontSize = with(LocalDensity.current) {
                        dimensionResource(id = R.dimen.char_size_mid).toSp()
                    },
                )
            )
        }
        Text(text = itemChartState.value.incomeTotal)
    }
    // Expense
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_EXPENSE)
        Text(
            text = stringResource(id = R.string.expense_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "-",
                style = TextStyle(
                    color = Color.Red,
                    fontSize = with(LocalDensity.current) {
                        dimensionResource(id = R.dimen.char_size_mid).toSp()
                    }
                )
            )
        }
        Text(text = itemChartState.value.expenseTotal)
    }
    // Balance
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
    ) {
        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_NONE)
        Text(
            text = stringResource(id = R.string.balance_colon)
        )
        Spacer(modifier = Modifier.weight(1f))
        val balance = (itemChartState.value.incomeTotal.toBigDecimal() -
                itemChartState.value.expenseTotal.toBigDecimal())

        if (balance > BigDecimal.ZERO) {
            Column(
                modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+",
                    style = TextStyle(
                        color = Color.Blue,
                        fontSize = with(LocalDensity.current) {
                            dimensionResource(id = R.dimen.char_size_mid).toSp()
                        },
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        else if (balance < BigDecimal.ZERO) {
            Column(
                modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "-",
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = with(LocalDensity.current) {
                            dimensionResource(id = R.dimen.char_size_mid).toSp()
                        },
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Text(
            text = balance.abs().toString(),
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
    }
}