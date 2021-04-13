package com.kakeibo.ui.settings.category.replace

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.R
import com.kakeibo.data.CategoryDsp
import com.kakeibo.ui.viewmodel.CategoryDspViewModel
import com.kakeibo.ui.viewmodel.KkbAppViewModel

class CategoryReorderActivity : AppCompatActivity(), EventClickListener {

    private lateinit var _fragmentReorder: CategoryReplaceReorderFragment

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private val _medium: Medium by viewModels()
    private val _categoryDspViewModel: CategoryDspViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_category_reorder)

        /*** hide home button on actionbar  */
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(false)
        }

        /*** ads  */
        _kkbAppViewModel.all.observe(this, {
            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (showAds) {
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        })

        _fragmentReorder = CategoryReplaceReorderFragment.newInstance()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frl_settings_category_reorder_container, _fragmentReorder)
                .commit()
    }

    override
    fun onNextPressed(tag: Int) {
        val dialog = AlertDialog.Builder(this)
        dialog.setIcon(R.mipmap.ic_mikan)
        dialog.setTitle(R.string.reorder_categories)
        dialog.setMessage(R.string.quest_determine_category_order)
        dialog.setPositiveButton(R.string.yes) { _, _ ->
            Toast.makeText(this, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show()

            _categoryDspViewModel.insertAll(_medium.newCategoryList.mapIndexed {
                index, category -> CategoryDsp(category.id, index, category.code)
            })
            finish()
        }
        dialog.setNegativeButton(R.string.no) { _, _ -> }
        dialog.show()
    }

    override
    fun onBackPressed(tag: Int) {
        super.onBackPressed()
    }
}