package com.kakeibo.feature_main.presentation.nav_drawer.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.kakeibo.BuildConfig
import com.kakeibo.R

@Composable
fun AboutScreen() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 10.dp
    ) {
        val context = LocalContext.current
        Column {
            Text(
                modifier = Modifier.padding(2.dp),
                text = stringResource(id = R.string.the_app_version),
                color = Color.Black,
                fontWeight = Bold
            )
            Text(
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 8.dp),
                text = "v: " + BuildConfig.VERSION_NAME,
                style = TextStyle(Color.Gray)
            )
            Text(
                modifier = Modifier.padding(2.dp),
                text = stringResource(id = R.string.how_to_use_app),
                color = Color.Black,
                fontWeight = Bold
            )
            ClickableText(
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 8.dp),
                text = AnnotatedString( stringResource(id = R.string.jump_to_website) ),
                onClick = {
                    val url = "https://sites.google.com/view/kakeibo/home/how-to-use-the-app"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                },
                style = TextStyle(MaterialTheme.colors.primary)
            )
        }
    }
}