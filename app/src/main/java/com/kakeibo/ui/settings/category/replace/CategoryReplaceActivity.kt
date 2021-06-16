package com.kakeibo.ui.settings.category.replace

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.ads.*
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryDsp
import com.kakeibo.databinding.ActivitySettingsCategoryReplaceBinding
import com.kakeibo.ui.viewmodel.CategoryDspViewModel
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.ui.viewmodel.KkbAppViewModel
import com.kakeibo.ui.viewmodel.SubscriptionViewModel
import java.util.*

class CategoryReplaceActivity : AppCompatActivity(), EventClickListener {

    companion object {
        private const val TAG = "CategoryReplaceActivity"
        private const val NUM_PAGES = 3
        var numColumns: Int = 0
    }

    private lateinit var _smartPagerAdapter: SmartPagerAdapter
    private lateinit var _viewPager: ViewPager2

    private var _showAds: Boolean = false
    private val _lstDots: MutableList<ImageView> = ArrayList()

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private val _subscriptionViewModel: SubscriptionViewModel by viewModels()
    private val _medium: Medium by viewModels()
    private val _categoryViewModel: CategoryViewModel by viewModels()
    private val _categoryDspViewModel: CategoryDspViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsCategoryReplaceBinding.inflate(layoutInflater)
        binding.kkbAppViewModel = _kkbAppViewModel
        binding.subscriptionViewModel = _subscriptionViewModel
        setContentView(binding.root)

        numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns)
        /* hide home button on actionbar  */
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setHomeButtonEnabled(false)
        }

        /* ads  */
        _kkbAppViewModel.all.observe(this, {
            _showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (_showAds) {
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d(TAG, "Ad loaded successfully.")
                    }

                    override fun onAdFailedToLoad(adError : LoadAdError) {
                        Log.e(TAG, adError.toString())
                    }

                    override fun onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdClosed() {
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }
                }
            }
        })

        _smartPagerAdapter = SmartPagerAdapter(this)
        _viewPager = findViewById(R.id.view_pager)
        _viewPager.offscreenPageLimit = 2
        _viewPager.adapter = _smartPagerAdapter
        _viewPager.isUserInputEnabled = false
        _viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    Medium.PAGE_1 -> _medium.setCurrentlyShown(Medium.PAGE_1)
                    Medium.PAGE_2 -> _medium.setCurrentlyShown(Medium.PAGE_2)
                    Medium.PAGE_3 -> _medium.setCurrentlyShown(Medium.PAGE_3)
                }
                super.onPageSelected(position)
            }
        })

        addDots()

        _categoryViewModel.dsp.observe(this, {
            _medium.newCategoryList.clear()
            _medium.newCategoryList.addAll(it)
        })
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
                    _lstDots[i].setImageDrawable(drawable)
                }
            }
        })
    }

    override
    fun onNextPressed(tag: Int) {
        when (tag) {
            CategoryReplaceRemoveFragment.TAG_INT -> {
                _viewPager.currentItem = CategoryReplaceAddFragment.TAG_INT
                (_smartPagerAdapter.fragments[1] as CategoryReplaceAddFragment).setRemainingCount()
            }
            CategoryReplaceAddFragment.TAG_INT -> {
                _viewPager.currentItem = CategoryReplaceReorderFragment.TAG_INT
                (_smartPagerAdapter.fragments[2] as CategoryReplaceReorderFragment).setGridItems()
            }
            CategoryReplaceReorderFragment.TAG_INT -> {
                /*** list: contains necessary categories ordered by location the user wants  */
                val dialog = AlertDialog.Builder(this)
                dialog.setIcon(R.mipmap.ic_mikan)
                dialog.setTitle(R.string.reorder_categories)
                dialog.setMessage(R.string.quest_determine_category_order)
                dialog.setPositiveButton(R.string.yes) { _, _ ->
                    Toast.makeText(this, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show()

                    _categoryDspViewModel.insertAll(_medium.newCategoryList.mapIndexed { index, category ->
                        CategoryDsp(category.id, index, category.code)
                    })
                    finish()
                }
                dialog.setNegativeButton(R.string.no) { _, _ -> }
                dialog.show()
            }
        }
    }

    override
    fun onBackPressed(tag: Int) {
        when (tag) {
            CategoryReplaceRemoveFragment.TAG_INT -> super.onBackPressed()
            CategoryReplaceAddFragment.TAG_INT -> {
                _viewPager.currentItem = CategoryReplaceRemoveFragment.TAG_INT
            }
            CategoryReplaceReorderFragment.TAG_INT -> {
                _viewPager.currentItem = CategoryReplaceAddFragment.TAG_INT
            }
        }
    }

    class SmartPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        val fragments: MutableList<Fragment> = mutableListOf()

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                CategoryReplaceRemoveFragment.TAG_INT -> {
                    val fragmentRemoval = CategoryReplaceRemoveFragment.newInstance()
                    fragments.add(fragmentRemoval)
                    fragmentRemoval
                }
                CategoryReplaceAddFragment.TAG_INT -> {
                    val fragmentAddition = CategoryReplaceAddFragment.newInstance()
                    fragments.add(fragmentAddition)
                    fragmentAddition
                }
                CategoryReplaceReorderFragment.TAG_INT -> {
                    val fragmentReorder = CategoryReplaceReorderFragment.newInstance()
                    fragments.add(fragmentReorder)
                    fragmentReorder
                }
                else -> {
                    val fragmentRemoval = CategoryReplaceRemoveFragment.newInstance()
                    fragments.add(fragmentRemoval)
                    fragmentRemoval
                }
            }
        }
    }
}