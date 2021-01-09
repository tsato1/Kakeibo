//package com.kakeibo.ui;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProviders;
//
//import com.kakeibo.R;
//import com.kakeibo.databinding.FragmentInputBinding;
//import com.kakeibo.databinding.FragmentReportBinding;
//import com.kakeibo.databinding.FragmentSearchBinding;
//
//public class TabFragment extends Fragment {
//
//    private static final String TAG = "TabFragment";
//
//    /**
//     * The fragment argument representing the section number for this
//     * fragment.
//     */
//    private static final String ARG_SECTION_NUMBER = "section_number";
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancedState) {
//        BillingViewModel billingViewModel =
//                ViewModelProviders.of(requireActivity()).get(BillingViewModel.class);
//        SubscriptionStatusViewModel subscriptionViewModel =
//                ViewModelProviders.of(requireActivity()).get(SubscriptionStatusViewModel.class);
//        ItemStatusViewModel itemStatusViewModel =
//                ViewModelProviders.of(requireActivity()).get(ItemStatusViewModel.class);
//        CategoryStatusViewModel categoryStatusViewModel =
//                ViewModelProviders.of(requireActivity()).get(CategoryStatusViewModel.class);
//        CategoryDspStatusViewModel categoryDspStatusViewModel =
//                ViewModelProviders.of(requireActivity()).get(CategoryDspStatusViewModel.class);
//
//        int section = getArguments().getInt(ARG_SECTION_NUMBER);
//
//        switch (section) {
//            case MainActivity.INPUT_PAGER_INDEX:
//                return createInputView(inflater, container, billingViewModel, subscriptionViewModel,
//                        categoryDspStatusViewModel);
//            case MainActivity.REPORT_PAGER_INDEX:
//                return createReportView(inflater, container, billingViewModel, subscriptionViewModel,
//                        itemStatusViewModel, categoryStatusViewModel);
//            case MainActivity.SEARCH_PAGER_INDEX:
//                return createSearchView(inflater, container, billingViewModel, subscriptionViewModel,
//                        categoryStatusViewModel);
//            default:
//                Log.e(TAG, "Unrecognized fragment index");
//                return createInputView(inflater, container, billingViewModel, subscriptionViewModel,
//                        categoryDspStatusViewModel);
//        }
//    }
//
//    private View createInputView(LayoutInflater inflater, ViewGroup container,
//                                 BillingViewModel billingViewModel,
//                                 SubscriptionStatusViewModel subscriptionViewModel,
//                                 CategoryDspStatusViewModel categoryDspStatusViewModel) {
//        FragmentInputBinding fragmentBinding =
//                DataBindingUtil.inflate(inflater, R.layout.fragment_input, container, false);
//        fragmentBinding.setLifecycleOwner(this);
//        fragmentBinding.setBillingViewModel(billingViewModel);
//        fragmentBinding.setSubscriptionViewModel(subscriptionViewModel);
//        fragmentBinding.setCategoryStatusViewModel(categoryDspStatusViewModel);
//        return fragmentBinding.getRoot();
//    }
//
//    private View createReportView(LayoutInflater inflater, ViewGroup container,
//                                BillingViewModel billingViewModel,
//                                SubscriptionStatusViewModel subscriptionViewModel,
//                                ItemStatusViewModel itemStatusViewModel,
//                                CategoryStatusViewModel categoryStatusViewModel) {
//        // Data binding with a ViewModel.
//        FragmentReportBinding fragmentBinding =
//                DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false);
//        fragmentBinding.setLifecycleOwner(this);
//        fragmentBinding.setBillingViewModel(billingViewModel);
//        fragmentBinding.setSubscriptionViewModel(subscriptionViewModel);
//        return fragmentBinding.getRoot();
//    }
//
//    private View createSearchView(LayoutInflater inflater, ViewGroup container,
//                                  BillingViewModel billingViewModel,
//                                  SubscriptionStatusViewModel subscriptionViewModel,
//                                  CategoryStatusViewModel categoryStatusViewModel) {
//        // Data binding with a ViewModel.
//        FragmentSearchBinding fragmentBinding = DataBindingUtil.inflate(
//                inflater, R.layout.fragment_search, container, false);
//        fragmentBinding.setLifecycleOwner(this);
//        fragmentBinding.setBillingViewModel(billingViewModel);
//        fragmentBinding.setSubscriptionViewModel(subscriptionViewModel);
//        return fragmentBinding.getRoot();
//    }
//
//    public static TabFragment newInstance(int sectionNumber) {
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        TabFragment tabFragment = new TabFragment();
//        tabFragment.setArguments(args);
//        return tabFragment;
//    }
//}
