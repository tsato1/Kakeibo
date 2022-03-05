package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.util.UtilCategory

@Composable
fun IncomeExpenseIndicator(
    categoryColor: Int
) {
    Box(
        modifier = Modifier
            .size(4.dp, 10.dp)
            .background(
                when (categoryColor) {
                    UtilCategory.CATEGORY_COLOR_INCOME -> {
                        MaterialTheme.colors.primary
                    }
                    UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                        MatchaGreen
                    }
                    else -> {
                        Color.Gray
                    }
                }
            )
    )
}