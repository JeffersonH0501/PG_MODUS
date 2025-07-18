package com.uniandes.modus

import com.uniandes.modus.cache.AppDatabase

class WasmConnectivityChecker : ConnectivityChecker {
    override fun isInternetAvailable(): Boolean {
        return true
    }
}

actual fun getConnectivityChecker(): ConnectivityChecker = WasmConnectivityChecker()

actual fun getCacheDataBase(): AppDatabase? = null