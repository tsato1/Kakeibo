package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle

@ExperimentalComposeUiApi
@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {//todo: put away keyboard when out of focus
//    val (focusRequester) = FocusRequester.createRefs()
//    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
//                .focusRequester(focusRequester)
                .onFocusChanged {
                    onFocusChange(it)
                },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.onSurface,
                backgroundColor = Color.Transparent
            )

//            keyboardActions = KeyboardActions(
//                onDone = { keyboardController?.hide() }
//            )
        )
        if (isHintVisible) {
            Text(
                text = hint,
                style = textStyle,
                color = Color.Gray
            )
        }
    }
}