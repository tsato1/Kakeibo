package com.kakeibo.ui.settings.category.edit

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.viewmodel.KkbAppViewModel
import com.kakeibo.util.UtilDate
import java.util.*

class CustomCategoryActivity : AppCompatActivity() {

    companion object {
        private const val NUM_PAGES = 3
    }

    private var _context: Context? = null

    private lateinit var _viewPager: ViewPager2
    private lateinit var _smartPagerAdapter: SmartPagerAdapter

    private val _lstDots: MutableList<ImageView> = ArrayList()

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private val _categoryStatusViewModel: CategoryStatusViewModel by viewModels()
    private val _customCategoryViewModel: CustomCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_custom_category)

        _context = this

        /* hide home button on actionbar */
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(false)
        }

        /* ads */
        _kkbAppViewModel.all.observe(this, {
            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (showAds) {
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
            }
        })

        _smartPagerAdapter = SmartPagerAdapter(this)
        _viewPager = findViewById(R.id.view_pager)
        _viewPager.adapter = _smartPagerAdapter
        _viewPager.isUserInputEnabled = false

        /* if this activity was called from CategoryEditionActivity */
        val id = intent.getLongExtra(CustomCategoryListActivity.EXTRA_KEY_CATEGORY_ID, -1)
        val code = intent.getIntExtra(CustomCategoryListActivity.EXTRA_KEY_CATEGORY_CODE, -1)
        _categoryStatusViewModel.allMap.observe(this, {
            when (code) {
                -1 -> {_customCategoryViewModel.reset()} /* for Category creation */
                else -> { /* for Category edit */
                    _customCategoryViewModel.setId(id)
                    _customCategoryViewModel.setCode(code)
                    _customCategoryViewModel.setColor(it[code]!!.color)
                    _customCategoryViewModel.setName(it[code]!!.name)
                    _customCategoryViewModel.setImage(it[code]!!.image!!)
                }
            }
        })

        /*** for dots indicator for pages  */
        addDots()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun addDots() {
        val dotsLayout = findViewById<LinearLayout>(R.id.dots)
        for (i in 0 until NUM_PAGES) {
            val dot = ImageView(this)
            if (i == 0) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_color_primary))
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_color_accent))
            }
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dotsLayout.addView(dot, params)
            _lstDots.add(dot)
        }
        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                for (i in 0 until NUM_PAGES) {
                    val drawableId = if (i == position) R.drawable.dot_color_primary else R.drawable.dot_color_accent
                    val drawable = ContextCompat.getDrawable(applicationContext, drawableId)
                    _lstDots.get(i).setImageDrawable(drawable)
                }
            }
        })
    }

    fun onNextPressed(tag: Int) {
        when (tag) {
            CustomCategoryColorFragment.TAG_INT -> {
                _viewPager.currentItem = CustomCategoryNameFragment.TAG_INT
            }
            CustomCategoryNameFragment.TAG_INT -> {
                _viewPager.currentItem = CustomCategoryImageFragment.TAG_INT
            }
            CustomCategoryImageFragment.TAG_INT -> {
                val dialog = AlertDialog.Builder(_context)
                dialog.setIcon(R.mipmap.ic_mikan)
                dialog.setTitle(R.string.create_category)
                dialog.setMessage(R.string.quest_category_creation_do_you_want_to_proceed)
                dialog.setPositiveButton(R.string.yes) { _, _ ->
                    /* Category creation */
                    if (_customCategoryViewModel.code.value == -1) {
                        _categoryStatusViewModel.insert(
                                CategoryStatus(
                                        0,
                                        _categoryStatusViewModel.getCodeForNewCustomCategory(),
                                        _customCategoryViewModel.name.value!!,
                                        _customCategoryViewModel.color.value!!,
                                        _customCategoryViewModel.significance.value!!,
                                        "", // not used because custom category
                                        _customCategoryViewModel.image.value!!,
                                        _customCategoryViewModel.parent.value!!,
                                        _customCategoryViewModel.description.value!!,
                                        UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_KMS)
                                ))
                        Toast.makeText(this, R.string.msg_category_created, Toast.LENGTH_LONG).show()
                    }
                    /* Category edit */
                    else {
                        _categoryStatusViewModel.insert(
                                CategoryStatus(
                                        _customCategoryViewModel.id.value!!,
                                        _customCategoryViewModel.code.value!!,
                                        _customCategoryViewModel.name.value!!,
                                        _customCategoryViewModel.color.value!!,
                                        _customCategoryViewModel.significance.value!!,
                                        "", // not used because custom category
                                        _customCategoryViewModel.image.value!!,
                                        _customCategoryViewModel.parent.value!!,
                                        _customCategoryViewModel.description.value!!,
                                        UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_KMS)
                                ))
                        Toast.makeText(this, R.string.msg_category_updated, Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
                dialog.setNegativeButton(R.string.no) { _, _ -> }
                dialog.show()
            }
        }
    }

    fun onBackPressed(tag: Int) {
        when (tag) {
            CustomCategoryColorFragment.TAG_INT -> super.onBackPressed()
            CustomCategoryNameFragment.TAG_INT -> _viewPager.currentItem = CustomCategoryColorFragment.TAG_INT
            CustomCategoryImageFragment.TAG_INT -> _viewPager.currentItem = CustomCategoryNameFragment.TAG_INT
        }
    }

    class SmartPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val fragments: MutableList<Fragment> = mutableListOf()

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                CustomCategoryColorFragment.TAG_INT -> {
                    val fragmentColor = CustomCategoryColorFragment.newInstance()
                    fragments.add(fragmentColor)
                    fragmentColor
                }
                CustomCategoryNameFragment.TAG_INT -> {
                    val fragmentLanguage = CustomCategoryNameFragment.newInstance()
                    fragments.add(fragmentLanguage)
                    fragmentLanguage
                }
                CustomCategoryImageFragment.TAG_INT -> {
                    val fragmentIcon = CustomCategoryImageFragment.newInstance()
                    fragments.add(fragmentIcon)
                    fragmentIcon
                }
                else -> {
                    val fragmentColor = CustomCategoryColorFragment.newInstance()
                    fragments.add(fragmentColor)
                    fragmentColor
                }
            }
        }
    }
}