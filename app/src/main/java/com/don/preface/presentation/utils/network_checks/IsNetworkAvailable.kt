package com.don.preface.presentation.utils.network_checks

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)

    val networkAvailability = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    Log.d("NetworkAvailability", "$networkAvailability")
    return networkAvailability
}

suspend fun isInternetAvailable(): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Attempt to connect to a common endpoint (Google DNS)
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Log.d("InternetAvailability", "true")
            true
        } catch (e: Exception) {
            Log.d("InternetAvailability", "false")
            false
        }
    }
}
