package com.uniandes.modus

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.uniandes.modus.cache.AppDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

private lateinit var applicationContext: Context

fun provideAndroidContext(context: Context) {
    applicationContext = context
}

class AndroidConnectivityChecker(private val context: Context) : ConnectivityChecker {

    override fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                trySend(false).isSuccess
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        trySend(isInternetAvailable()).isSuccess

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}

actual fun getConnectivityChecker(): ConnectivityChecker {
    return AndroidConnectivityChecker(applicationContext)
}

private var databaseInstance: AppDatabase? = null

actual fun getCacheDataBase(): AppDatabase? {
    if (databaseInstance == null) {
        val driver = AndroidSqliteDriver(AppDatabase.Schema, applicationContext, "cache.db")
        databaseInstance = AppDatabase(driver)

        val tables = getAllTables(driver)
        println("Tablas en la base de datos: $tables")
    }
    return databaseInstance
}

fun getAllTables(driver: SqlDriver): List<String> {
    val query = "SELECT name FROM sqlite_master WHERE type='table';"

    val queryResult: QueryResult<List<String>> = driver.executeQuery(
        0,
        query,
        { cursor ->
            val tables = mutableListOf<String>()
            while (cursor.next().value) {
                val tableName = cursor.getString(0)
                if (tableName != null) {
                    tables.add(tableName)
                }
            }
            QueryResult.Value(tables)
        },
        0,
        null
    )

    return (queryResult as QueryResult.Value).value
}