package com.kakeibo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.billingclient.api.Purchase
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.data.CategoryStatus
import com.kakeibo.settings.SettingsCompatActivity
import com.kakeibo.ui.adapter.CategoryStatusViewModel
import com.kakeibo.ui.search.TabFragment3
import com.kakeibo.util.QueryBuilder
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 0
        private var fabStart: FloatingActionButton? = null
        private var fabEnd: FloatingActionButton? = null
    }

    private lateinit var _myFragmentPagerAdapter: SmartFragmentStatePagerAdapter

    private lateinit var _viewPager: ViewPager

    private lateinit var _billingClientLifecycle: BillingClientLifecycle

    private lateinit var _authenticationViewModel: FirebaseUserViewModel
    private lateinit var _billingViewModel: BillingViewModel
    private lateinit var _subscriptionViewModel: SubscriptionStatusViewModel

    var allCategoryStatusMap = hashMapOf<Int, CategoryStatus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        _myFragmentPagerAdapter = SmartPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        _viewPager = findViewById(R.id.viewpager)

        val tabs = findViewById<TabLayout>(R.id.tabs)
        _viewPager.setAdapter(_myFragmentPagerAdapter)
        _viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(_viewPager))

        _authenticationViewModel = ViewModelProviders.of(this)[FirebaseUserViewModel::class.java]
        _billingViewModel = ViewModelProviders.of(this)[BillingViewModel::class.java]
        _subscriptionViewModel = ViewModelProviders.of(this)[SubscriptionStatusViewModel::class.java]
        val categoryStatusViewModel = ViewModelProviders.of(this)[CategoryStatusViewModel::class.java]

        _billingClientLifecycle = (application as SubApp).billingClientLifecycle
        lifecycle.addObserver(_billingClientLifecycle)

        // Register purchases when they change.
        _billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            it?.let {
                registerPurchases(it)
            }
        })

        // Launch billing flow when user clicks button to buy something.
        _billingViewModel.buyEvent.observe(this, {
            it?.let {
                _billingClientLifecycle.launchBillingFlow(this@MainActivity, it)
            }
        })

        // Open the Play Store when event is triggered.
        _billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, {
            Log.i(TAG, "Viewing subscriptions on the Google Play Store")
            val sku = it
            val url = if (sku == null) {
                // If the SKU is not specified, just open the Google Play subscriptions URL.
                Constants.PLAY_STORE_SUBSCRIPTION_URL
            } else {
                // If the SKU is specified, open the deeplink for this SKU on Google Play.
                String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL, sku, packageName)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
//            startActivity(intent)
        })

        // Update authentication UI.
        _authenticationViewModel.firebaseUser.observe(this, {
            invalidateOptionsMenu()
            if (it == null) {
//                triggerSignIn()
            } else {
                Log.d(TAG, "CURRENT user: " + it.email + " " + it.displayName)
            }
        })

        // Update subscription information when user changes.
        _authenticationViewModel.userChangeEvent.observe(this, {
            _subscriptionViewModel.userChanged()
            _billingClientLifecycle.purchaseUpdateEvent.value?.let {
                registerPurchases(it)
            }
        })

        categoryStatusViewModel.all.observe(this, {
            for (category in it) {
                allCategoryStatusMap[category.code] = category
            }
//            QueryBuilder.init(it)
        })

        fabStart = findViewById(R.id.fab_start)
        fabEnd = findViewById(R.id.fab_end)
        fabStart!!.setOnClickListener(FabClickListener())
        fabEnd!!.setOnClickListener(FabClickListener())
    }

    /**
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsCompatActivity::class.java))
                true
            }
            R.id.sign_in -> {
                triggerSignIn()
                true
            }
            R.id.sign_out -> {
                triggerSignOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Update menu based on sign-in state. Called in response to [.invalidateOptionsMenu].
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isSignedIn = _authenticationViewModel.isSignedIn()
        menu.findItem(R.id.sign_in).isVisible = !isSignedIn
        menu.findItem(R.id.sign_out).isVisible = isSignedIn
        return true
    }

    private fun refreshData() {
        _billingClientLifecycle.queryPurchases()
        _subscriptionViewModel.manualRefresh()
    }

    /*
     * Sign in with FirebaseUI Auth.
     */
    private fun triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!")
        val providers: MutableList<IdpConfig> = ArrayList()
        // Configure the different methods users can sign in
        providers.add(EmailBuilder().build())
        providers.add(GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    /**
     * Sign out with FirebaseUI Auth.
     */
    private fun triggerSignOut() {
        _subscriptionViewModel.unregisterInstanceId()
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            Log.d(TAG, "User SIGNED OUT!")
            _authenticationViewModel.updateFirebaseUser()
        }
    }

    /**
     * Receive Activity result, including sign-in result.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                // If sign-in is successful, update ViewModel.
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Sign-in SUCCESS!")
                    _authenticationViewModel.updateFirebaseUser()
                } else {
                    Log.d(TAG, "Sign-in FAILED!")
                }
            }
            else -> {
                Log.e(TAG, "Unrecognized request code: $requestCode")
            }
        }
    }

    class SmartPagerAdapter(fragmentManager: FragmentManager?) : SmartFragmentStatePagerAdapter(fragmentManager) {
        // Returns total number of pages
        override fun getCount(): Int {
            return NUM_ITEMS
        }

        // Returns the fragment to display for that page
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    fabStart!!.visibility = View.GONE
                    fabEnd!!.visibility = View.GONE
                    TabFragment1.newInstance()
                }
                1 -> {
                    fabStart!!.visibility = View.GONE
                    fabEnd!!.setImageResource(R.drawable.ic_cloud_upload_white)
                    fabEnd!!.visibility = View.VISIBLE
                    TabFragment2.newInstance()
                }
                2 -> {
                    fabStart!!.setImageResource(R.drawable.ic_add_white)
                    fabStart!!.visibility = View.VISIBLE
                    fabEnd!!.setImageResource(R.drawable.ic_search_white)
                    fabEnd!!.visibility = View.VISIBLE
                    TabFragment3.newInstance()
                }
                else -> {
                    fabStart!!.visibility = View.GONE
                    fabEnd!!.visibility = View.GONE
                    TabFragment1.newInstance()
                }
            }
        }

        // Returns the page title for the top indicator
        override fun getPageTitle(position: Int): CharSequence? {
            return "Page $position"
        }

        companion object {
            private const val NUM_ITEMS = 3
        }
    }

    /*
     * Called from TabFragment1 upon tapping one of the category buttons
     */
    fun onItemSaved(query: Query, eventDate: String?) {
        _viewPager.currentItem = 1 // move to tabFragment2
        Log.d(TAG, "onItemSaved() queryC=" + query.queryC)
        Log.d(TAG, "onItemSaved() queryD=" + query.queryD)
        try {
            (_myFragmentPagerAdapter.getItem(1) as TabFragment2)
                    .focusOnSavedItem(query, eventDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Called from TabFragment3 upon tapping search button
     */
    fun onSearch(query: Query, fromDate: String?, toDate: String?) {
        _viewPager.currentItem = 1 // move to tabFragment2
        Log.d(TAG, "onSearch() queryC=" + query.queryC)
        Log.d(TAG, "onSearch() queryD=" + query.queryD)
        try {
            (_myFragmentPagerAdapter.getItem(1) as TabFragment2)
                    .onSearch(query, fromDate, toDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inner class FabClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            if (view.id == R.id.fab_start) {
                if (_viewPager.currentItem == 2) {
                    (_myFragmentPagerAdapter.getRegisteredFragment(2) as TabFragment3)
                            .addCriteria()
                }
            } else if (view.id == R.id.fab_end) {
                if (_viewPager.currentItem == 1) {
                    (_myFragmentPagerAdapter.getRegisteredFragment(1) as TabFragment2)
                            .export()
                } else if (_viewPager.currentItem == 2) {
                    (_myFragmentPagerAdapter.getRegisteredFragment(2) as TabFragment3)
                            .doSearch()
                }
            }
        }
    }
}