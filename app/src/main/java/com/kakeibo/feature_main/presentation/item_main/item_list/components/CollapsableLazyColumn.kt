package com.kakeibo.feature_main.presentation.item_main.item_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.components.IncomeExpenseIndicator
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItem
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilDate.toYMDWString
import kotlinx.datetime.toLocalDate

@Composable
fun CollapsableLazyColumn(
    navController: NavController,
    sections: List<ExpandableItem>,
    dateFormatIndex: Int,
    modifier: Modifier
) {
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }

    if (sections.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_item_found)
            )
        }
    }
    else {
        LazyColumn(modifier) {
            sections.forEachIndexed { i, expandableItem ->
                val collapsed = collapsedState[i]
                item(key = "header_$i") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 4.dp)
                            .clickable {
                                collapsedState[i] = !collapsed
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.run {
                                if (collapsed)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
                            contentDescription = "",
                            tint = Color.LightGray,
                        )
                        Text(
                            text = expandableItem.parent.date
                                .toLocalDate()
                                .toYMDWString(UtilDate.DATE_FORMATS[dateFormatIndex]),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_INCOME)
                        Text(
                            text = expandableItem.parent.income
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_EXPENSE)
                        Text(
                            text = expandableItem.parent.expense
                        )
                    }
                    Divider()
                }
                if (!collapsed) {
                    items(expandableItem.children) { child ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 2.dp, 12.dp, 2.dp)
                                .clickable {
                                    navController.navigate(
                                        Screen.ItemDetailScreen.route + "?itemId=${child.id}"
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIcon(
                                code = child.categoryCode,
                                drawable = child.categoryDrawable,
                                image = child.categoryImage
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp, 2.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = child.categoryName
                                )
                                Text(
                                    text = child.memo
                                )
                            }
                            Text(text = child.amount)
                        }
                        Divider()
                    }
                }
            }
        }
    }
}