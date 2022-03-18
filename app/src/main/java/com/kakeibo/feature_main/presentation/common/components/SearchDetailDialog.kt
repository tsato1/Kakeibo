package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.domain.models.SearchModel

@Composable
fun SearchDetailDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    searchModel: SearchModel?
) {
    DialogCard(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.search_criteria),
        content = {
            searchModel?.let {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.date_range))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (searchModel.fromDate == null)
                            "Not specified"
                        else
                            searchModel.fromDate + " - " + searchModel.toDate,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(id = R.string.amount_range))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (searchModel.fromAmount == null)
                            "Not specified"
                        else
                            searchModel.fromAmount + " - " + searchModel.toAmount,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(id = R.string.category))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = searchModel.categoryName ?: "Not specified",
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = stringResource(id = R.string.memo))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = searchModel.memo ?: "Not specified",
                        textAlign = TextAlign.End
                    )
                }
            }?: Text(text = "search is null")
        },
        positiveButton = {
            OutlinedButton(
                onClick = { onConfirmButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}