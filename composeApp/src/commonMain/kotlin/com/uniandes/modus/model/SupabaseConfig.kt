package com.uniandes.modus.model

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

class SupabaseConfig private constructor() {

    val client = createSupabaseClient(
        supabaseUrl = "https://bolqpuqsquhjfobudhzy.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJvbHFwdXFzcXVoamZvYnVkaHp5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAxNTI5NDgsImV4cCI6MjA1NTcyODk0OH0.Q3T4gVejCvfdZATFbEgXquUaBGFL7ZIgMIOBbd-Cv24"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }

    companion object {
        val instance: SupabaseConfig by lazy { SupabaseConfig() }
    }
}