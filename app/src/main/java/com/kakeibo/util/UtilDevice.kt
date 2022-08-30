package com.kakeibo.util

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

fun getIpAddress(): String {
    var ip = ""
    try {
        val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
        while (enumNetworkInterfaces.hasMoreElements()) {
            val networkInterface: NetworkInterface = enumNetworkInterfaces.nextElement()
            val enumInetAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
            while (enumInetAddress.hasMoreElements()) {
                val inetAddress: InetAddress = enumInetAddress.nextElement()
                if (inetAddress.isSiteLocalAddress) {
                    ip += inetAddress.hostAddress
                }
            }
        }
    } catch (e: SocketException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
        ip += "Something Wrong! $e".trimIndent()
    }
    return ip
}