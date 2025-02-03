package ke.don.common_datasource.data.repositoryImpl

import android.util.Log
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import ke.don.common_datasource.data.di.SupabaseClient
import ke.don.common_datasource.data.di.UserManager
import ke.don.common_datasource.domain.getters.generateNonce
import ke.don.common_datasource.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile

class ProfileRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val userProfile : UserManager
): ProfileRepository {
    override val rawNonce: String
    override val hashedNonce: String

    private val supabase = supabaseClient.supabase

    init {
        val (raw, hashed) = generateNonce()
        rawNonce = raw
        hashedNonce = hashed
    }

    override suspend fun signInAndCheckProfile(
        idToken: String,
        displayName: String?,
        profilePictureUri: String?
    ) {
        // Step 1: Sign in with Supabase using Google ID token
        supabase.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
            nonce = rawNonce
        }

            val user = supabase.auth.currentUserOrNull()

            userProfile.onAuthStateChange()
            val result = if (user != null) {
                supabase.from("profiles")
                    .select() {
                    filter {
                        Profile::authId eq user.id
                        //or
                        eq("name", "The Shire")
                    }
                }
                    .decodeSingle<Profile>()

            } else {
                null
            }

        Log.d("ProfileRepositoryImpl", "signInAndCheckProfile: $result")

        val isProfilePresent = result == null

        Log.d("ProfileRepositoryImpl", "Profile is present? : $isProfilePresent")

//                // Step 2: Check if the user already has a profile photo
//                if (existingProfilePhoto.isNullOrEmpty()) {
//                    updateProfilePhoto(supabase, profilePictureUri)
//                }
//            }

    }

    private suspend fun updateProfilePhoto() {
//        val response = supabase.auth.updateUserMetadata(
//            userMetadata = mapOf("profile_photo" to photoUrl)
//        )
//        if (response.isSuccess) {
//            println("Profile photo updated successfully!")
//        } else {
//            println("Error updating profile photo: ${response.error?.message}")
//        }
//
//    }


    }
}