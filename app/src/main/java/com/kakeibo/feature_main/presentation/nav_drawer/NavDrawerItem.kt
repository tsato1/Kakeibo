package com.kakeibo.feature_main.presentation.nav_drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.graphics.vector.ImageVector
import com.kakeibo.R

sealed class NavDrawerItem(val route: String, val label: Int, val icon: ImageVector) {
    object SignIn : NavDrawerItem("sign_in", R.string.sign_in, Icons.Default.Login)
    object SignOut : NavDrawerItem("sign_out", R.string.sign_out, Icons.Default.Logout)
    object SignInKakeibo : NavDrawerItem("login", R.string.login, Icons.Default.Login)
    object SignOutKakeibo : NavDrawerItem("logout", R.string.logout, Icons.Default.Logout)
    object About : NavDrawerItem("about", R.string.about_this_app, Icons.Default.Info)
}