package com.kakeibo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kakeibo.R
import com.kakeibo.ui.theme.dimens

@Composable
fun DialogCard(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.dialogPadding)
                .clip(RoundedCornerShape(MaterialTheme.dimens.dialogRoundedCorner))
                .background(MaterialTheme.colors.background)
                .onGloballyPositioned { coordinates ->
                    // This will be the size of the Column.
                    coordinates.size
                    // The position of the Column relative to the application window.
                    coordinates.positionInWindow()
                    // The position of the Column relative to the Compose root.
                    coordinates.positionInRoot()
                    // These will be the alignment lines provided to the layout (empty here for Column).
                    coordinates.providedAlignmentLines
                    // This will a LayoutCoordinates instance corresponding to the parent of Column.
                    coordinates.parentLayoutCoordinates
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        MaterialTheme.dimens.dialogTitlePaddingHorizontal,
                        MaterialTheme.dimens.dialogTitlePaddingVertical
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.ic_mikan),
                    contentDescription = "",
                    tint= Color.Unspecified
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.button
                )
            }
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.dialogPadding))
                content()
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.dialogPadding))
            }
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                negativeButton?.let {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        val textStyle = MaterialTheme.typography.body2
                        ProvideTextStyle(textStyle, it)
                    }
                }
                positiveButton?.let {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.medium
                    ) {
                        val textStyle = MaterialTheme.typography.body2
                        ProvideTextStyle(textStyle, it)
                    }
                }
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.dialogPadding))
            }
        }
    }
}