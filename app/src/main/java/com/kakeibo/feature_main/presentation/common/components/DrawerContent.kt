package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.FirebaseViewModel
import com.kakeibo.feature_main.presentation.nav_drawer.NavDrawerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    onSigninClick: () -> Unit,
    onSignoutClick: () -> Unit,
    viewModel: FirebaseViewModel = hiltViewModel()
) {

    Column(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.onSurface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = viewModel.firebaseUser.value?.photoUrl
                                ?: R.mipmap.ic_launcher_round,
                            builder = {
                                size(OriginalSize)
                                scale(Scale.FIT)
                                transformations(CircleCropTransformation())
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }
                Text(
                    text = viewModel.firebaseUser.value?.displayName ?: stringResource(id = R.string.not_signed_in),
                    color = MaterialTheme.colors.background
                )
                Text(
                    text = viewModel.firebaseUser.value?.email ?: "",
                    color = MaterialTheme.colors.background
                )
            }
        }
        Spacer(modifier = Modifier
            .height(6.dp)
            .fillMaxWidth()
        )
        if (viewModel.isSignedIn()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onSignoutClick() })
                    .height(45.dp)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = stringResource(id = R.string.sign_out),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = stringResource(id = R.string.sign_out),
                    color = Color.Black
                )
            }
        }
        else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onSigninClick() })
                    .height(45.dp)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = stringResource(id = R.string.sign_in),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = stringResource(id = R.string.sign_in),
                    color = Color.Black
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clickable {
                    navController.navigate(NavDrawerItem.About.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true

                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }

                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.mipmap.ic_mikan),
                contentDescription = "About"
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = stringResource(id = R.string.about_this_app),
                color = Color.Black
            )
        }

    }
}