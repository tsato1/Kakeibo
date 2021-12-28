package com.kakeibo.feature_main.presentation.item_main.item_list.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing

@Composable
fun CollapsableLazyColumn(
    sections: List<ExpandableItem>,
    modifier: Modifier
) {
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }
    LazyColumn(modifier) {
        sections.forEachIndexed { i, expandableItem ->
            val collapsed = collapsedState[i]
            item(key = "header_$i") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        modifier = Modifier.padding(vertical = 10.dp),
                        text = expandableItem.parent.date,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp),
                        text = expandableItem.parent.income
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp),
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
                            .padding(4.dp)
                    ) {
                        when {
                            child.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START -> {
                                Image(
                                    painter = painterResource(
                                        id = UtilDrawing.getDrawableIdFromIconName(
                                            LocalContext.current,
                                            child.categoryDrawable
                                        )
                                    ),
                                    contentDescription = "Category Icon"
                                )
                            }
                            child.categoryImage != null -> {
                                val size = dimensionResource(id = R.dimen.new_category_drawable_size)
                                Image(
                                    modifier = Modifier.size(size),
                                    bitmap = UtilDrawing.bytesToBitmap(child.categoryImage)?.let {
                                        UtilDrawing.getBitmapClippedCircle(it).asImageBitmap()
                                    } ?: Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888).asImageBitmap(),
                                    contentDescription = "Category Icon"
                                )
                            }
                            else -> {
                                Icon(
                                    painter = painterResource(id = R.mipmap.ic_launcher),
                                    contentDescription = "Icon Not Found"
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = child.categoryName
                            )
                            Text(
                                text = child.memo,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                        }
                        Text(
                            text = child.amount
                        )
                    }
                    Divider()
                }
            }
        }
    }
}

data class ExpandableItem(
    val parent: Parent,
    val children: List<DisplayedItemModel>
) {

    data class Parent(val date: String, val income: String, val expense: String)

}