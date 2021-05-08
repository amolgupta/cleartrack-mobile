package xyz.getclear.android.common

import android.content.Context
import android.net.ConnectivityManager
import xyz.getclear.data.net.contract.NetworkConnectivityUseCase

class NetworkConnectivityUseCaseImpl constructor(private val context: Context) : NetworkConnectivityUseCase {
    override fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}