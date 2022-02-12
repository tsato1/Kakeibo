package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.domain.models.SearchModel

@Composable
fun SearchDetailAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    searchModel: SearchModel?
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                Text(text = stringResource(id = R.string.search_criteria))
            }
        },
        onDismissRequest = { onDismissRequest() },
        text = {
            searchModel?.let {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.date_range))
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = if (searchModel.fromDate == null)
                            "Not specified"
                        else
                            searchModel.fromDate + " - " + searchModel.toDate
                    )
                    Text(text = stringResource(id = R.string.amount_range))
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = if (searchModel.fromAmount == null)
                            "Not specified"
                        else
                            searchModel.fromAmount + " - " + searchModel.toAmount
                    )
                    Text(text = stringResource(id = R.string.category))
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = searchModel.categoryName ?: "Not specified"
                    )
                    Text(text = stringResource(id = R.string.memo))
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = searchModel.memo ?: "Not specified"
                    )
                }
            } ?: Text(text = "searchModel is null")
        },
        dismissButton = {},
        confirmButton = {
            OutlinedButton(
                onClick = { onConfirmButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}