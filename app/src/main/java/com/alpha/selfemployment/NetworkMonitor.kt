package com.alpha.selfemployment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NetworkStatus {
    WifiWithInternet,
    WifiNoInternet,
    CellularWithInternet,
    CellularNoInternet,
    Online, // New general online state
    Offline
}



class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Initialize with current network status
    private val _status = MutableStateFlow(getInitialStatus(connectivityManager))
    val status = _status.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkStatus(network)
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            updateNetworkStatus(network)
        }

        override fun onLost(network: Network) {
            _status.value = NetworkStatus.Offline
        }
    }

    private fun updateNetworkStatus(network: Network) {
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return

        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        _status.value = when {
            (isWifi && hasInternet) || (isCellular && hasInternet) -> NetworkStatus.Online
            else -> NetworkStatus.Offline
        }
    }

    private fun getInitialStatus(cm: ConnectivityManager): NetworkStatus {
        val network = cm.activeNetwork ?: return NetworkStatus.Offline
        val caps = cm.getNetworkCapabilities(network) ?: return NetworkStatus.Offline

        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        return if ((isWifi && hasInternet) || (isCellular && hasInternet)) {
            NetworkStatus.Online
        } else {
            NetworkStatus.Offline
        }
    }

    fun register() {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

@Composable
fun rememberNetworkStatus(): State<NetworkStatus> {
    val context = LocalContext.current
    val monitor = remember { NetworkMonitor(context) }
    val networkStatus by monitor.status.collectAsState()

    // Register/unregister callback
    DisposableEffect(monitor) {
        monitor.register()
        onDispose { monitor.unregister() }
    }

    return derivedStateOf { networkStatus }
}