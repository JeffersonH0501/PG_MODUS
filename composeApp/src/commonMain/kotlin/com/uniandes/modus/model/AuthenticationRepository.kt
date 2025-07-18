package com.uniandes.modus.model

import com.uniandes.modus.getCacheDataBase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthenticationRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client
    private val dbCache = getCacheDataBase()!!

    private val _isLoggedIn = MutableStateFlow(getCurrentUser() != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    companion object {
        val instance: AuthenticationRepository by lazy { AuthenticationRepository() }
    }

    suspend fun authenticate(document: String, password: String): Boolean {
        return try {
            val user = supabase.postgrest["users"]
                .select {
                    filter {
                        eq("document", document)
                    }
                }
                .decodeSingleOrNull<UserRepository.User>()

            if (user == null) {
                return false
            }

            supabase.auth.signInWith(Email) {
                this.email = user.email
                this.password = password
            }

            dbCache.userCacheQueries.deleteUser()
            dbCache.userCacheQueries.insertOrReplace(
                user.document,
                user.name,
                user.email,
                user.phone,
                user.department,
                user.city,
                user.address,
                user.role
            )

            _isLoggedIn.value = true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun logOut(): Boolean {
        return try {
            supabase.auth.signOut()
            dbCache.userCacheQueries.deleteUser()
            dbCache.paymentCacheQueries.deleteAll()
            dbCache.cardCacheQueries.deleteAll()

            _isLoggedIn.value = false
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCurrentUser(): UserRepository.User? {
        return try {
            dbCache.userCacheQueries.selectUser().executeAsOneOrNull()?.let { user ->
                UserRepository.User(
                    document = user.document,
                    name = user.name,
                    email = user.email,
                    phone = user.phone,
                    department = user.department,
                    city = user.city,
                    address = user.address,
                    role = user.role
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}