package com.kakeibo

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kakeibo.billing.BillingClientLifecycle
import com.kakeibo.data.DataRepository
import com.kakeibo.data.disk.AppDatabase
import com.kakeibo.data.disk.LocalDataSource
import com.kakeibo.data.network.WebDataSource
import com.kakeibo.data.network.firebase.FakeServerFunctions
import com.kakeibo.data.network.firebase.ServerFunctions
import com.kakeibo.data.network.firebase.ServerFunctionsImpl
import com.kakeibo.util.UtilDate
import java.util.*

/*
 * Android Application class. Used for accessing singletons.
 */
class SubApp : Application() {

    private val executors = AppExecutors()

    private val database: AppDatabase
        get() = AppDatabase.getInstance(this)

    private val localDataSource: LocalDataSource
        get() = LocalDataSource.getInstance(executors, database)

    private val serverFunctions: ServerFunctions
        get() = if (Constants.USE_FAKE_SERVER) {
            FakeServerFunctions.getInstance()
        } else {
            ServerFunctionsImpl.getInstance()
        }

    private val webDataSource: WebDataSource
        get() = WebDataSource.getInstance(executors, serverFunctions)

    val billingClientLifecycle: BillingClientLifecycle
        get() = BillingClientLifecycle.getInstance(this)

    val repository: DataRepository
        get() = DataRepository.getInstance(localDataSource, webDataSource, billingClientLifecycle)

    override fun onCreate() {
        super.onCreate()
        instance = this
        PreferenceManager.setDefaultValues(instance, R.xml.preferences, false)
        preferences = PreferenceManager.getDefaultSharedPreferences(instance)
    }

    companion object {
        private lateinit var preferences: SharedPreferences
        private lateinit var instance: SubApp
        private val sharedPreferences: SharedPreferences
            get() {
                PreferenceManager.setDefaultValues(instance, R.xml.preferences, false)
                preferences = PreferenceManager.getDefaultSharedPreferences(instance)
                return preferences
            }

        /* dateFormat  */
        fun getDateFormat(key: Int): Int {
            val strKey = instance.getString(key)
            val dateFormatIndex = sharedPreferences.getString(strKey, UtilDate.DATE_FORMAT_YMD)
            return dateFormatIndex!!.toInt()
        }

        /* fraction digits  */
        fun getFractionDigits(key: Int): Int {
            val strKey = instance.getString(key)
            val locale = Locale.getDefault()
            var defValue = 0
            try {
                val currency = Currency.getInstance(locale)
                defValue = currency.defaultFractionDigits
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            val digitsIndex = sharedPreferences.getString(strKey, "" + defValue)
            val fractionDigits = instance.resources.getStringArray(R.array.pref_list_fraction_digits)
            return fractionDigits[digitsIndex!!.toInt()].toInt()
        }

        /* num category icons per row  */
        fun getNumColumns(key: Int): Int {
            val strKey = instance.getString(key)
            val numColumnsIndex = sharedPreferences.getString(strKey, "1")
            val numColumns = instance.resources.getStringArray(R.array.pref_list_num_columns)
            return numColumns[numColumnsIndex!!.toInt()].toInt()
        }
    }
}