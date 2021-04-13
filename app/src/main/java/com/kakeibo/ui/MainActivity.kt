package com.kakeibo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.Purchase
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.data.CategoryStatus
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.settings.AboutActivity
import com.kakeibo.ui.settings.InAppPurchasesActivity
import com.kakeibo.ui.settings.SettingsActivity
import com.kakeibo.ui.viewmodel.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 0

        lateinit var allCategoryList: List<CategoryStatus>
        lateinit var allDspCategoryList: List<CategoryStatus>

        lateinit var weekNames: Array<String>
        var dateFormat: Int = 0
        var numColumns: Int = 0

        private lateinit var fabStart: FloatingActionButton
        private lateinit var fabEnd: FloatingActionButton
    }

    private lateinit var _smartPagerAdapter: SmartPagerAdapter
    private lateinit var _viewPager: ViewPager2
    private lateinit var _drawerLayout: DrawerLayout
    private lateinit var _navView: NavigationView

    private val _kkbAppViewModel: KkbAppViewModel by viewModels()
    private lateinit var _billingClientLifecycle: BillingClientLifecycle
    private val _authenticationViewModel: FirebaseUserViewModel by viewModels()
    private val _billingViewModel: BillingViewModel by viewModels()
    private val _subscriptionViewModel: SubscriptionStatusViewModel by viewModels()
    private val _medium: Medium by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weekNames = resources.getStringArray(R.array.week_name)
        dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        _drawerLayout = findViewById(R.id.drawer_layout)

        _navView = findViewById(R.id.nav_view)
        _navView.setNavigationItemSelectedListener(this)
        val navHeaderView = _navView.getHeaderView(0)

        val toggle = ActionBarDrawerToggle(this, _drawerLayout, toolbar, R.string.open, R.string.close)
        _drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        _smartPagerAdapter = SmartPagerAdapter(this)
        _viewPager = findViewById(R.id.view_pager)
        _viewPager.offscreenPageLimit = 2
        _viewPager.adapter = _smartPagerAdapter
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        TabLayoutMediator(tabLayout, _viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.input)
                1 -> tab.text = getString(R.string.report)
                2 -> tab.text = getString(R.string.search)
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        _medium.setCurrentlyShown(Medium.FRAGMENT_INPUT)
                        fabStart.visibility = View.GONE
                        fabEnd.visibility = View.GONE
                    }
                    1 -> {
                        _medium.setCurrentlyShown(Medium.FRAGMENT_REPORT)
                        fabStart.visibility = View.GONE
                        fabEnd.setImageResource(R.drawable.ic_cloud_upload_white)
                        fabEnd.visibility = View.VISIBLE
                    }
                    2 -> {
                        _medium.setCurrentlyShown(Medium.FRAGMENT_SEARCH)
                        fabStart.setImageResource(R.drawable.ic_add_white)
                        fabStart.visibility = View.VISIBLE
                        fabEnd.setImageResource(R.drawable.ic_search_white)
                        fabEnd.visibility = View.VISIBLE
                    }
                    else -> {
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        _kkbAppViewModel.all.observe(this, {
            val showAds = it?.valInt2 == 0 // val2 = -1:original, 0:agreed to show ads

            if (showAds) {
                MobileAds.initialize(this) {}
                val adView: AdView = findViewById(R.id.ad_container)
                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)
                adView.visibility = View.VISIBLE
            }
        })

        _billingClientLifecycle = (application as SubApp).billingClientLifecycle
        lifecycle.addObserver(_billingClientLifecycle)

        /* Register purchases when they change. */
        _billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            it?.let {
                registerPurchases(it)
            }
        })

        /* Launch billing flow when user clicks button to buy something. */
        _billingViewModel.buyEvent.observe(this, {
            it?.let {
                _billingClientLifecycle.launchBillingFlow(this@MainActivity, it)
            }
        })

        /* Open the Play Store when event is triggered. */
        _billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, {
            Log.i(TAG, "Viewing subscriptions on the Google Play Store")
            val sku = it
            val url = if (sku == null) {
                /* If the SKU is not specified, just open the Google Play subscriptions URL. */
                Constants.PLAY_STORE_SUBSCRIPTION_URL
            } else {
                /* If the SKU is specified, open the deeplink for this SKU on Google Play. */
                String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL, sku, packageName)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
        })

        _authenticationViewModel.firebaseUser.observe(this, {
            invalidateOptionsMenu()

            if (it == null) {
                navHeaderView.findViewById<ImageView>(R.id.imv_user_profile).setBackgroundResource(R.mipmap.ic_launcher_round)
                navHeaderView.findViewById<TextView>(R.id.txv_user_name).text = getString(R.string.app_name)
                navHeaderView.findViewById<TextView>(R.id.txv_user_email).text = getString(R.string.not_signed_in)
                Log.d(TAG, "currently NOT logged in to firebase")
            } else {
                navHeaderView.findViewById<ImageView>(R.id.imv_user_profile).setImageURI(it.photoUrl)
                navHeaderView.findViewById<TextView>(R.id.txv_user_name).text = it.displayName
                navHeaderView.findViewById<TextView>(R.id.txv_user_email).text = it.email
                Log.d(TAG, "CURRENT user: " + it.email + " " + it.displayName)
            }
        })

        /* Update subscription information when user changes. */
        _authenticationViewModel.userChangeEvent.observe(this, {
            _subscriptionViewModel.userChanged()
            _billingClientLifecycle.purchaseUpdateEvent.value?.let {
                registerPurchases(it)
            }
        })

        val categoryStatusViewModel: CategoryStatusViewModel by viewModels()
        categoryStatusViewModel.all.observe(this, {
            allCategoryList = it
        })
        categoryStatusViewModel.dsp.observe(this, {
            allDspCategoryList = it
        })

        fabStart = findViewById(R.id.fab_start)
        fabEnd = findViewById(R.id.fab_end)
        fabStart.setOnClickListener(FabClickListener())
        fabEnd.setOnClickListener(FabClickListener())
    }

    override fun onStart() {
        super.onStart()
        refreshData()
    }

//    override fun onResume() {
//        super.onResume()
//        _smartPagerAdapter?.let {
//
//        }
//    } // todo _smartPagerAdapter == null ????

    override fun onBackPressed() {
        if (_viewPager.currentItem <= 3) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            _viewPager.currentItem = _viewPager.currentItem - 1
        }
    }

    /*
     * Register SKUs and purchase tokens with the server.
     */
    private fun registerPurchases(purchaseList: List<Purchase>) {
        for (purchase in purchaseList) {
            val sku = purchase.sku
            val purchaseToken = purchase.purchaseToken
            Log.d(TAG, "Register purchase with sku: $sku, token: $purchaseToken")
            _subscriptionViewModel.registerSubscription(sku, purchaseToken)
        }
    }

    private fun refreshData() {
        _billingClientLifecycle.queryPurchases()
        _subscriptionViewModel.manualRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val isSignedIn = _authenticationViewModel.isSignedIn()
        _navView.menu.get(0).isVisible = !isSignedIn
        _navView.menu.get(1).isVisible = isSignedIn

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_in -> {
//                startActivity(Intent(this, SignInActivity::class.java))
                triggerSignIn()
                true
            }
            R.id.sign_out -> {
                triggerSignOut()
                true
            }
            R.id.in_app_purchases -> {
                startActivity(Intent(this, InAppPurchasesActivity::class.java))
                true
            }
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> true
        }
    }

    /*
     * Sign in with FirebaseUI Auth.
     */
    private fun triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!")
        val providers: MutableList<IdpConfig> = ArrayList()
        providers.add(EmailBuilder().build())
        providers.add(GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    /*
     * Sign out with FirebaseUI Auth.
     */
    private fun triggerSignOut() {
        _subscriptionViewModel.unregisterInstanceId()
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Log.d(TAG, "User SIGNED OUT!")
            _authenticationViewModel.updateFirebaseUser()
            Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_LONG).show()
        }
    }

    /*
     * Receive Activity result, including sign-in result.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        refreshData()
        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_LONG).show()
                    _authenticationViewModel.updateFirebaseUser()
                } else {
                    Toast.makeText(this, R.string.sign_in_failure, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Log.e(TAG, "Unrecognized request code: $requestCode")
            }
        }
    }

    class SmartPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        val fragments: MutableList<Fragment> = mutableListOf()

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment1 = InputFragment.newInstance()
                    fragments.add(fragment1)
                    fragment1
                }
                1 -> {
                    val fragment2 = ReportFragment.newInstance()
                    fragments.add(fragment2)
                    fragment2
                }
                2 -> {
                    val fragment3 = SearchFragment.newInstance()
                    fragments.add(fragment3)
                    fragment3
                }
                else -> throw Exception("unknown item type")
            }
        }
    }

    /*
     * Called from TabFragment1 upon tapping one of the category buttons
     */
    fun onItemSaved(date: String) {
        _viewPager.currentItem = 1 // move to tabFragment2
        (_smartPagerAdapter.fragments[1] as ReportFragment).focusOnSavedItem(date)
    }

    /*
     * Called from TabFragment3 upon tapping search button
     */
    fun onSearch(query: Query) {
        _viewPager.currentItem = 1 // move to tabFragment2
        (_smartPagerAdapter.fragments[1] as ReportFragment).onSearch(query)
    }

    internal inner class FabClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.fab_start) {
                if (_viewPager.currentItem == 2) {
                    (_smartPagerAdapter.fragments[2] as SearchFragment).addCriteria()
                }
            }
            else if (view.id == R.id.fab_end) {
                if (_viewPager.currentItem == 1) {
                    (_smartPagerAdapter.fragments[1] as ReportFragment).export()
                } else if (_viewPager.currentItem == 2) {
                    (_smartPagerAdapter.fragments[2] as SearchFragment).doSearch()
                }
            }
        }
    }
}