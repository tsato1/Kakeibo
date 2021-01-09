package com.kakeibo.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;
import com.kakeibo.Constants;
import com.kakeibo.R;
import com.kakeibo.SubApp;
import com.kakeibo.billing.BillingClientLifecycle;
import com.kakeibo.settings.SettingsCompatActivity;
import com.kakeibo.ui.search.TabFragment3;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 0;

    private SmartFragmentStatePagerAdapter _myFragmentPagerAdapter;
    private ViewPager _viewPager;

    private static FloatingActionButton fabStart;
    private static FloatingActionButton fabEnd;

    private BillingClientLifecycle _billingClientLifecycle;

    private FirebaseUserViewModel _authenticationViewModel;
    private BillingViewModel _billingViewModel;
    private SubscriptionStatusViewModel _subscriptionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        _myFragmentPagerAdapter = new SmartPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        _viewPager = findViewById(R.id.viewpager);
        TabLayout tabs = findViewById(R.id.tabs);
        _viewPager.setAdapter(_myFragmentPagerAdapter);
        _viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(_viewPager));

        _authenticationViewModel = ViewModelProviders.of(this).get(FirebaseUserViewModel.class);
        _billingViewModel = ViewModelProviders.of(this).get(BillingViewModel.class);
        _subscriptionViewModel = ViewModelProviders.of(this).get(SubscriptionStatusViewModel.class);

        _billingClientLifecycle = ((SubApp) getApplication()).getBillingClientLifecycle();
        getLifecycle().addObserver(_billingClientLifecycle);

        // Register purchases when they change.
        _billingClientLifecycle.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });

        // Launch billing flow when user clicks button to buy something.
        _billingViewModel.buyEvent.observe(this, new Observer<BillingFlowParams>() {
            @Override
            public void onChanged(BillingFlowParams billingFlowParams) {
                if (billingFlowParams != null) {
                    _billingClientLifecycle
                            .launchBillingFlow(MainActivity.this, billingFlowParams);
                }
            }
        });

        // Open the Play Store when event is triggered.
        _billingViewModel.openPlayStoreSubscriptionsEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String sku) {
                Log.i(TAG, "Viewing subscriptions on the Google Play Store");
                String url;
                if (sku == null) {
                    // If the SKU is not specified, just open the Google Play subscriptions URL.
                    url = Constants.PLAY_STORE_SUBSCRIPTION_URL;
                } else {
                    // If the SKU is specified, open the deeplink for this SKU on Google Play.
                    url = String.format(Constants.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
                            sku, getApplicationContext().getPackageName());
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // Update authentication UI.
        final Observer<FirebaseUser> fireaseUserObserver = new Observer<FirebaseUser>() {
            @Override
            public void onChanged(@Nullable final FirebaseUser firebaseUser) {
                invalidateOptionsMenu();
                if (firebaseUser == null) {
//                    triggerSignIn();
                } else {
                    Log.d(TAG, "Current user: "
                            + firebaseUser.getEmail() + " " + firebaseUser.getDisplayName());
                }
            }
        };
        _authenticationViewModel.firebaseUser.observe(this, fireaseUserObserver);

        // Update subscription information when user changes.
        _authenticationViewModel.userChangeEvent.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void aVoid) {
                _subscriptionViewModel.userChanged();
                List<Purchase> purchases = _billingClientLifecycle.purchaseUpdateEvent.getValue();
                if (purchases != null) {
                    registerPurchases(purchases);
                }
            }
        });

        fabStart = findViewById(R.id.fab_start);
        fabEnd = findViewById(R.id.fab_end);
        fabStart.setOnClickListener(new FabClickListener());
        fabEnd.setOnClickListener(new FabClickListener());
    }

    /**
     * Register SKUs and purchase tokens with the server.
     */
    private void registerPurchases(List<Purchase> purchaseList) {
        for (Purchase purchase : purchaseList) {
            String sku = purchase.getSku();
            String purchaseToken = purchase.getPurchaseToken();
            Log.d(TAG, "Register purchase with sku: " + sku + ", token: " + purchaseToken);
            _subscriptionViewModel.registerSubscription(sku, purchaseToken);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsCompatActivity.class));
            return true;
        } else if (id == R.id.sign_in) {
            startActivity(new Intent(this, GoogleSignInActivity.class));
            return true;
        } else if (id == R.id.sign_out) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update menu based on sign-in state. Called in response to {@link #invalidateOptionsMenu}.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isSignedIn = _authenticationViewModel.isSignedIn();
        menu.findItem(R.id.sign_in).setVisible(!isSignedIn);
        menu.findItem(R.id.sign_out).setVisible(isSignedIn);
        return true;
    }

    private void refreshData() {
        _billingClientLifecycle.queryPurchases();
        _subscriptionViewModel.manualRefresh();
    }

    /*
     * Sign in with FirebaseUI Auth.
     */
    private void triggerSignIn() {
        Log.d(TAG, "Attempting SIGN-IN!");
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        // Configure the different methods users can sign in
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    /*
     * Sign out with FirebaseUI Auth.
     */
    private void triggerSignOut() {
        _subscriptionViewModel.unregisterInstanceId();
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User SIGNED OUT!");
                        _authenticationViewModel.updateFirebaseUser();
                    }
                });
    }

    /*
     * Receive Activity result, including sign-in result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If sign-in is successful, update ViewModel.
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Sign-in SUCCESS!");
                _authenticationViewModel.updateFirebaseUser();
            } else {
                Log.d(TAG, "Sign-in FAILED!");
            }
        } else {
            Log.e(TAG, "Unrecognized request code: " + requestCode);
        }
    }

    public static class SmartPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final static int NUM_ITEMS = 3;

        public SmartPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    fabStart.setVisibility(View.GONE);
                    fabEnd.setVisibility(View.GONE);
                    return TabFragment1.newInstance();
                case 1:
                    fabStart.setVisibility(View.GONE);
                    fabEnd.setImageResource(R.drawable.ic_cloud_upload_white);
                    fabEnd.setVisibility(View.VISIBLE);
                    return TabFragment2.newInstance();
                case 2:
                    fabStart.setImageResource(R.drawable.ic_add_white);
                    fabStart.setVisibility(View.VISIBLE);
                    fabEnd.setImageResource(R.drawable.ic_search_white);
                    fabEnd.setVisibility(View.VISIBLE);
                    return TabFragment3.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    /*
     * Called from TabFragment1 upon tapping one of the category buttons
     */
    public void onItemSaved(Query query, String eventDate) {
        _viewPager.setCurrentItem(1); // move to tabFragment2

        Log.d(TAG, "onItemSaved() queryC="+query.getQueryC());
        Log.d(TAG, "onItemSaved() queryD="+query.getQueryD());

        try {
            ((TabFragment2) _myFragmentPagerAdapter.getItem(1))
                    .focusOnSavedItem(query, eventDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Called from TabFragment3 upon tapping search button
     */
    public void onSearch(Query query, String fromDate, String toDate) {
        _viewPager.setCurrentItem(1); // move to tabFragment2

        Log.d(TAG, "onSearch() queryC="+query.getQueryC());
        Log.d(TAG, "onSearch() queryD="+query.getQueryD());

        try {
            ((TabFragment2) _myFragmentPagerAdapter.getItem(1))
                    .onSearch(query, fromDate,toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class FabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.fab_start) {
                if (_viewPager.getCurrentItem() == 2) {
                    ((TabFragment3) _myFragmentPagerAdapter.getRegisteredFragment(2))
                            .addCriteria();
                }
            } else if (view.getId() == R.id.fab_end) {
                if (_viewPager.getCurrentItem() == 1) {
                    ((TabFragment2) _myFragmentPagerAdapter.getRegisteredFragment(1))
                            .export();
                } else if (_viewPager.getCurrentItem() == 2) {
                    ((TabFragment3) _myFragmentPagerAdapter.getRegisteredFragment(2))
                            .doSearch();
                }
            }
        }
    }
}
