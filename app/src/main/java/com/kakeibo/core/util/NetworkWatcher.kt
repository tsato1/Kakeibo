package com.kakeibo.core.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class NetworkWatcher
@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
private constructor(
    application: Application
) {

    private val connectivityManager =
        application.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    // general availability of Internet over any type
    var isOnline = false
        get() {
            updateFields()
            return field
        }

    var isOverWifi = false
        get() {
            updateFields()
            return field
        }

    var isOverCellular = false
        get() {
            updateFields()
            return field
        }

    var isOverEthernet = false
        get() {
            updateFields()
            return field
        }

    companion object {
        @Volatile
        private var INSTANCE: NetworkWatcher? = null

        fun getInstance(application: Application): NetworkWatcher {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = NetworkWatcher(application)
                }
                return INSTANCE!!
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun updateFields() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val networkAvailability =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (networkAvailability != null &&
                networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            ) {
                //has network
                isOnline = true

                // wifi
                isOverWifi =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

                // cellular
                isOverCellular =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

                // ethernet
                isOverEthernet =
                    networkAvailability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } else {
                isOnline = false
                isOverWifi = false
                isOverCellular = false
                isOverEthernet = false
            }
        } else {

            val info = connectivityManager.activeNetworkInfo
            if (info != null && info.isConnected) {
                isOnline = true

                val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                isOverWifi = wifi != null && wifi.isConnected

                val cellular = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                isOverCellular = cellular != null && cellular.isConnected

                val ethernet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
                isOverEthernet = ethernet != null && ethernet.isConnected

            } else {
                isOnline = false
                isOverWifi = false
                isOverCellular = false
                isOverEthernet = false
            }
        }
    }

    fun watchNetwork(): Flow<Boolean> = watchWifi()
        .combine(watchCellular()) { wifi, cellular -> wifi || cellular }
        .combine(watchEthernet()) { wifiAndCellular, ethernet -> wifiAndCellular || ethernet }

    fun watchNetworkAsLiveData(): LiveData<Boolean> = watchNetwork().asLiveData()

    fun watchWifi(): Flow<Boolean> = callbackFlowForType(NetworkCapabilities.TRANSPORT_WIFI)

    fun watchWifiAsLiveData() = watchWifi().asLiveData()

    fun watchCellular(): Flow<Boolean> = callbackFlowForType(NetworkCapabilities.TRANSPORT_CELLULAR)

    fun watchCellularAsLiveData() = watchCellular().asLiveData()

    fun watchEthernet(): Flow<Boolean> = callbackFlowForType(NetworkCapabilities.TRANSPORT_ETHERNET)

    fun watchEthernetAsLiveData() = watchEthernet().asLiveData()

    private fun callbackFlowForType(@androidx.annotation.IntRange(from = 0, to = 7) type: Int) = callbackFlow {

        this.trySend(false).isSuccess

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(type)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                this@callbackFlow.trySend(false).isSuccess
            }

            override fun onUnavailable() {
                this@callbackFlow.trySend(false).isSuccess
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                // do nothing
            }

            override fun onAvailable(network: Network) {
                this@callbackFlow.trySend(true).isSuccess
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}