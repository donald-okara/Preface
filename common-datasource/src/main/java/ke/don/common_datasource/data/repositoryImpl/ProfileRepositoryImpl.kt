package ke.don.common_datasource.data.repositoryImpl

import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import ke.don.common_datasource.domain.getters.generateNonce
import ke.don.common_datasource.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile

class ProfileRepositoryImpl(
    supabaseClient: ke.don.common_datasource.data.di.SupabaseClient
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

    private val supabase = supabaseClient.supabase

    override val isSignedIn = supabase.auth.currentSessionOrNull() != null

    init {
        val (raw, hashed) = generateNonce()
        rawNonce = raw
        hashedNonce = hashed
    }

    override suspend fun signInAndCheckProfile(idToken: String, displayName: String?, profilePictureUri: String?) {
        try {
            // Step 1: Sign in with Supabase using the Google ID token
            supabase.auth.signInWith(IDToken){
                this.idToken = idToken
                provider = Google
                nonce = rawNonce
            }

            // Step 2: Check if a profile exists for the user
            val userId = supabase.auth.currentUserOrNull()?.id
            if (userId == null) {
                throw IllegalStateException("User ID is null after sign-in")
            }

            val existingProfile = supabase
                .from("profiles")
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Profile>()

            if (existingProfile == null) {
                // Step 3: If no profile exists, create a new one
                createUserProfile(userId, displayName, profilePictureUri)
                Log.d("ProfileCreation", "New profile created for user: $userId")
            } else {
                Log.d("ProfileCreation", "Profile already exists for user: $userId")
            }
        } catch (e: Exception) {
            Log.e("ProfileCreation", "Error during sign-in or profile creation: ${e.message}")
            throw e
        }
    }

    private suspend fun createUserProfile(userId: String, displayName: String?, profilePictureUri: String?) {
        try {
            // Create a new profile for the user
            val profile = Profile(
                id = userId,
                name = displayName ?: "Unknown",
                avatarUrl = profilePictureUri ?: ""
            )

            // Insert the profile into the 'profiles' table
            supabase.from("profiles").insert(profile)
            Log.d("ProfileCreation", "New profile created for user: $userId")
        } catch (e: Exception) {
            Log.e("ProfileCreation", "Error creating profile: ${e.message}")
            throw e
        }
    }

}