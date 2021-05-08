package xyz.getclear.data.net.contract

interface NetworkConnectivityUseCase {
    fun isNetworkAvailable(): Boolean
}