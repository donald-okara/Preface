package ke.don.common_datasource.remote.data.di

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import ke.don.shared_domain.BuildConfig

object SupabaseClientProvider {
    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ){
        install(Postgrest)
        install(Auth)
        install(Functions)
    }
}