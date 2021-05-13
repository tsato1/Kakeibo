package com.kakeibo.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kakeibo.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_about)
    }

    /* call set up in layout xml */
    fun onClick(v: View) {
        if (v.id == R.id.txv_how_to_use_app) {
            val url = "https://sites.google.com/view/kakeibo/home/how-to-use-the-app"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }
}