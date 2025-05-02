package ke.don.common_datasource.remote.domain.error_handler

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors the device's internet availability and emits a Flow of Boolean values.
 * Emits `true` when the device has an active internet connection (Wi-Fi, cellular, etc.),
 * and `false` when no internet is available.
 *
 * Requires the `ACCESS_NETWORK_STATE` permission in the AndroidManifest.xml:
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */

@Singleton
class InternetAvailability @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Emits `true` whenever there is at least one network
     * (Wi-Fi or cellular) with validated Internet access,
     * `false` otherwise.
     */
    val isInternetAvailable: Flow<Boolean> = callbackFlow {
        // Helper to check current state
        fun hasInternet(): Boolean {
            val nw = connectivityManager.activeNetwork ?: return false
            val caps = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        // Initial emission
        trySend(hasInternet())

        // Build a NetworkRequest looking for Internet-capable networks
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Callback will push updates into the flow
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(hasInternet())
            }
        }

        connectivityManager.registerNetworkCallback(request, callback)
        // Clean up when the flow collector is gone
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }
}
