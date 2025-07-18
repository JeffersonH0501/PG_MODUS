package com.uniandes.modus.model

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

class UserRepository private constructor() {

    private val supabase = SupabaseConfig.instance.client

    companion object {
        val instance: UserRepository by lazy { UserRepository() }
    }

    @Serializable
    data class User(
        val document: String,
        val name: String,
        val email: String,
        val phone: String,
        val department: String,
        val city: String,
        val address: String,
        val role: String,
    )

    suspend fun add(name: String, email: String, phone: String, department: String, city: String, address: String, document: String): Boolean {
        return try {

            val user = User(
                document = document,
                name = name,
                email = email,
                phone = phone,
                department = department,
                city = city,
                address = address,
                role = "User"
            )

            supabase.postgrest["users"].insert(user)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}