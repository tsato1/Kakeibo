package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.ui.theme.MatchaGreen

/*
Once search is performed, this compose item will be shown on the top of the ItemMain screen
instead of DatePickerRow
 */

@Composable
fun SearchModeTopRow(
    onCloseButtonClick: () -> Unit,
    onTextButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        IconButton(
            modifier = Modifier
                .background(MatchaGreen)
                .size(30.dp)
                .clip(RoundedCornerShape(6.dp)),
            onClick = {
                onCloseButtonClick()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = Color.White,
                contentDescription = null
            )
        }
        TextButton(
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = {
                onTextButtonClick()
            }
        ) {
            Text(
                text = stringResource(id = R.string.search_result)
            )
        }
    }
}