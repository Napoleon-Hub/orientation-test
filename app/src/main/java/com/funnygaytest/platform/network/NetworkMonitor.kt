package com.funnygaytest.platform.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isConnected: Flow<Boolean> = callbackFlow {
        val validNetworks: MutableSet<Network> = Collections.synchronizedSet(mutableSetOf())

        fun publish() {
            trySend(validNetworks.isNotEmpty())
        }

        fun update(network: Network, capabilities: NetworkCapabilities?) {
            if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                validNetworks.add(network)
            } else {
                validNetworks.remove(network)
            }
            publish()
        }

        connectivityManager.activeNetwork?.let { network ->
            update(network, connectivityManager.getNetworkCapabilities(network))
        }
        publish()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                update(network, connectivityManager.getNetworkCapabilities(network))
            }

            override fun onLost(network: Network) {
                validNetworks.remove(network)
                publish()
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                update(network, capabilities)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
