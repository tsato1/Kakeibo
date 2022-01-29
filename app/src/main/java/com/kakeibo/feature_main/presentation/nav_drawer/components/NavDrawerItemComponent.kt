//package com.kakeibo.feature_main.presentation.nav_drawer.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Icon
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.kakeibo.feature_main.presentation.nav_drawer.NavDrawerItem
//
//@Composable
//fun NavDrawerItem(item: NavDrawerItem, selected: Boolean, onItemClick: (NavDrawerItem) -> Unit) {
//    val background = if (selected) MaterialTheme.colors.onPrimary else Color.Transparent
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = { onItemClick(item) })
//            .height(45.dp)
//            .background(color = background)
//            .padding(start = 10.dp)
//    ) {
//        Icon(
//            imageVector = item.icon,
//            contentDescription = stringResource(id = item.label),
//            modifier = Modifier
//                .height(32.dp)
//                .width(32.dp)
//        )
//        Spacer(modifier = Modifier.width(7.dp))
//        Text(
//            text = stringResource(id = item.label),
//            fontSize = 18.sp,
//            color = Color.Black
//        )
//    }
//}