package com.kakeibo.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kakeibo.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesImpl @Inject constructor(
    @ApplicationContext val context: Context
) : AppPreferences {

    private var instance: SharedPreferences

    init {
        val nonEncryptedPreferences: SharedPreferences =
//            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
            PreferenceManager.getDefaultSharedPreferences(context)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            instance = initializeEncryptedSharedPreferencesManager()
//            if (nonEncryptedPreferences.all.isNotEmpty()) {
//                // migrate non encrypted shared preferences
//                // to encrypted shared preferences and clear them once finished.
//                nonEncryptedPreferences.copyTo(instance)
//                nonEncryptedPreferences.clear()
//            }
//        } else {
            instance = nonEncryptedPreferences
//        }
    }
//
//    private fun initializeEncryptedSharedPreferencesManager(): SharedPreferences {
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
////        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC) //?? which is better
//        return EncryptedSharedPreferences.create(
//            context,
//            Constants.ENCRYPTED_SHARED_PREF_NAME,
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }

    /* dateFormat */
    override
    fun getDateFormatIndex(): Int {
        return getInt(
            context.resources.getString(R.string.pref_key_date_format), 0
        )
    }
    override
    fun getDateFormat(): String {
        val dateFormats = context.resources.getStringArray(R.array.pref_list_date_format)
        return dateFormats[getDateFormatIndex()]
    }

    /* fraction digits */
    override
    fun getFractionDigitsIndex(): Int {
        return getInt(
            context.resources.getString(R.string.pref_key_fraction_digits), 0
        )
    }
    override
    fun getFractionDigits(): Int {
        val fractionDigits = context.resources.getStringArray(R.array.pref_list_fraction_digits)
        return fractionDigits[getFractionDigitsIndex()].toInt()
    }

    /* num category icons per row */
    override
    fun getNumColumnsIndex(): Int {
        return getInt(
            context.resources.getString(R.string.pref_key_num_columns), 1
        )
    }
    override
    fun getNumColumns(): Int {
        val numColumns = context.resources.getStringArray(R.array.pref_list_num_columns)
        return numColumns[getNumColumnsIndex()].toInt()
    }




    override fun <T : Any?> set(key: Int, value: T) {
        instance.set(context.resources.getString(key), value)
    }

    override fun getString(key: String, defaultValue: String?): String {
        val value = getValue(key, defaultValue)
        return value.toString()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = getValue(key, defaultValue)
        return value.toString().toInt()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = getValue(key, defaultValue)
        return value.toString().toBoolean()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        val value = getValue(key, defaultValue)
        return value.toString().toLong()
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        val value = getValue(key, defaultValue)
        return value as Float
    }

    private fun getValue(key: String, defaultValue: Any?): Any? {
        var value = instance.all[key]
        value = value ?: defaultValue
        return value
    }

    override fun contains(key: String): Boolean {
        return instance.contains(key)
    }

    override fun remove(key: String) {
        instance.remove(key)
    }

    override fun clear() {
        instance.clear()
    }
}