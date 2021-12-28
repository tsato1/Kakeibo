//package com.kakeibo.ui.settings
//
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Bundle
//import android.view.MenuItem
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.activityViewModels
//import androidx.preference.Preference
//import androidx.preference.PreferenceFragmentCompat
//import com.kakeibo.R
//import com.kakeibo.ui.settings.category.edit.CustomCategoryListActivity
////import com.kakeibo.ui.settings.category.replace.CategoryReplaceActivity
//import com.kakeibo.feature_settings.presentation.category_reorder.CategoryReorderActivity
//import com.kakeibo.feature_item.presentation.item_list.ItemViewModel
//import com.kakeibo.ui.viewmodel.KkbAppViewModel
//
//class SettingsFragment : PreferenceFragmentCompat() {
//
//    private var _showAds: Boolean = false
//
//    private val _itemViewModel: ItemViewModel by activityViewModels()
//    private val _kkbAppViewModel: KkbAppViewModel by activityViewModels()
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        addPreferencesFromResource(R.xml.preferences)
//
//        _kkbAppViewModel.all.observe( this, {
//            _showAds = it?.valInt2==0 // val2 = -1:original, 0:agreed to show ads
//        })
//    }
//
//    override fun onPreferenceTreeClick(preference: Preference): Boolean {
//        return when (preference.key) {
//            getString(R.string.pref_key_date_format) -> {
//                true
//            }
//            getString(R.string.pref_key_fraction_digits) -> {
//                true
//            }
//            getString(R.string.pref_key_start_date_of_month) -> {
//                true
//            }
//            getString(R.string.pref_key_category_add_remove_reorder) -> {
//                if (!_showAds) {
//                    handleAgreement()
//                    return false
//                }
////                startActivity(Intent(activity, CategoryReplaceActivity::class.java))
//                true
//            }
//            getString(R.string.pref_key_category_reorder) -> {
//                if (!_showAds) {
//                    handleAgreement()
//                    return false
//                }
//                startActivity(Intent(activity, CategoryReorderActivity::class.java))
//                true
//            }
//            getString(R.string.pref_key_category_list) -> {
//                if (!_showAds) {
//                    handleAgreement()
//                    return false
//                }
//                startActivity(Intent(activity, CustomCategoryListActivity::class.java))
//                true
//            }
//            getString(R.string.pref_key_export_periodically) -> {
//
//                true
//            }
//            getString(R.string.pref_key_export_period) -> {
//                true
//            }
//            getString(R.string.pref_key_delete_all_data) -> {
//                val dialog = AlertDialog.Builder(requireContext())
//                dialog.setIcon(R.mipmap.ic_mikan)
//                dialog.setTitle(getString(R.string.delete_all_items))
//                dialog.setMessage(getString(R.string.desc_delete_all_items))
//                dialog.setPositiveButton(R.string.ok) { _, _ ->
//                    val confirmation = AlertDialog.Builder(requireContext())
//                    confirmation.setIcon(R.drawable.ic_warning_black_24dp)
//                    confirmation.setTitle(getString(R.string.delete_all_items))
//                    confirmation.setMessage(getString(R.string.warn_delete_all_items))
//                    confirmation.setPositiveButton(R.string.ok) { _, _ ->
////                        _itemViewModel.deleteAll()
//                    }
//                    confirmation.setNegativeButton(R.string.cancel) { _, _ -> }
//                    confirmation.show()
//                }
//                dialog.setNegativeButton(R.string.cancel) { _, _ -> }
//                dialog.show()
//                true
//            }
////            getString(R.string.pref_key_in_app_purchases) -> {
////                startActivity(Intent(activity, InAppPurchasesActivity::class.java))
////                true
////            }
////            getString(R.string.pref_key_about) -> {
////                startActivity(Intent(activity, AboutActivity::class.java))
////                true
////            }
//            else -> {
//                super.onPreferenceTreeClick(preference)
//            }
//        }
//    }
//
//    private fun handleAgreement() {
//        val dialog = AlertDialog.Builder(requireContext())
//        dialog.setIcon(R.mipmap.ic_mikan)
//        dialog.setTitle(R.string.category_management)
//        dialog.setMessage(getString(R.string.quest_do_you_want_to_manage_categories))
//        dialog.setPositiveButton(R.string.yes) { d: DialogInterface, _ ->
//            d.dismiss()
//            val dialog2 = AlertDialog.Builder(requireContext())
//            dialog2.setIcon(R.drawable.ic_warning_black_24dp)
//            dialog2.setTitle(R.string.warning)
//            dialog2.setMessage(getString(R.string.quest_irreversible_operation_do_you_want_to_proceed))
//            dialog2.setPositiveButton(R.string.yes) { _, _ ->
//                _kkbAppViewModel.updateVal2(0)
//            }
//            dialog2.setNegativeButton(R.string.no, null)
//            dialog2.create()
//            dialog2.show()
//        }
//        dialog.setNegativeButton(R.string.no, null)
//        dialog.create()
//        dialog.show()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            requireActivity().onBackPressed()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}