package com.kakeibo.core.presentation.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.auth.presentation.AuthResult
import com.kakeibo.auth.presentation.AuthViewModel

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) { // one of viewModel or context changes, effect fires up
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.BadRequest -> {
                    Toast.makeText(context, "bad request", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "BadRequest")
                }
                is AuthResult.ConnectionError -> {
                    Toast.makeText(context, "Cannot connect to server. Try again later.", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "ConnectionError")
                }
                is AuthResult.Authorized -> {
                    Toast.makeText(context, "Logged in to Kakeibo.", Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                    Log.d("asdf AuthScreen", "Authorized")
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(context, "Not Authorized!!", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "Unauthorized")
                }
                is AuthResult.NoContent -> {
                    Toast.makeText(context, "Deleted an item Successfully", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "NoContent")
                }
                is AuthResult.UserAlreadyExists -> {
                    Toast.makeText(context, "User already exists", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "UserAlreadyExists")
                }
                is AuthResult.UserNotInDatabase -> {
                    Toast.makeText(context, "User not in db", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "UserNotInDatabase")
                }
                is AuthResult.InvalidEmailOrPassword -> {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "InvalidEmailOrPassword")
                }
                is AuthResult.NotOnline -> {
                    Toast.makeText(context, "You are not online!!", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "NotOnline")
                }
                is AuthResult.DifferentDevice -> {
                    Toast.makeText(context, "Logged in from Different Device!!", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "Different Device detected")
                }
                is AuthResult.Canceled -> {
                    Toast.makeText(context, "Canceled!!", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "Canceled")
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(context, "Unknown Error occurred!!", Toast.LENGTH_LONG).show()
                    Log.d("asdf AuthScreen", "UnknownError")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = state.registerEmail,
            onValueChange = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.RegisterEmailChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Email")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.registerPassword,
            onValueChange = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.RegisterPasswordChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Password")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.Register)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Sign up")
        }

        Spacer(modifier = Modifier.height(64.dp))

        TextField(
            value = state.loginEmail,
            onValueChange = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.LoginEmailChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Email")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = state.loginPassword,
            onValueChange = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.LoginPasswordChanged(it))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Password")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.onEvent(AuthViewModel.AuthUiEvent.Login)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Sign in")
        }
    }
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}