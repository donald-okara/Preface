package ke.don.common_datasource.remote.data.profile.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.local.datastore.token.TokenData
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import ke.don.common_datasource.local.datastore.token.tokenDatastore
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.SupabaseBook
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.values.BOOKS
import ke.don.shared_domain.values.BOOKSHELFCATALOG
import ke.don.shared_domain.values.BOOKSHELFTABLE
import ke.don.shared_domain.values.MAX_RETRIES
import ke.don.shared_domain.values.PROFILESTABLE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update


class ProfileNetworkClass(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val tokenDatastore: TokenDatastoreManager
) {
    /**
     * CREATE
     */
    suspend fun signIn(
        idToken: String,
        rawNonce: String
    ): NetworkResult<UserInfo>{
        return try {
            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
                nonce = rawNonce
            }
            val userInfo =  supabase.auth.currentUserOrNull()

            tokenDatastore.setToken(
                supabase.auth.currentAccessTokenOrNull()
            )
            if(userInfo != null){
                NetworkResult.Success(userInfo)
            }else{
                NetworkResult.Error(
                    message = "User is not logged in"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            NetworkResult.Error(
                message = e.message.toString()
            )
        }

    }

    suspend fun insertUserProfile(
        profile : Profile
    ): NetworkResult<NoDataReturned>{
        return try {
            supabase.from(PROFILESTABLE).insert(profile)
            Log.d(TAG, "Profile inserted successfully")

            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to create profile. Please try again later", Toast.LENGTH_SHORT).show()
            NetworkResult.Error(
                message = e.message.toString()
            )
        }
    }

    /**
     * READ
     */
    suspend fun checkIfProfileIsPresent(userId: String): NetworkResult<Boolean>{
        return try {
            val response = supabase.from("profiles")
                .select {
                    filter {
                        Profile::authId eq userId
                        //or
                        eq("auth_id", userId)
                    }
                }
                .decodeSingleOrNull<Profile>()

            NetworkResult.Success(response != null)
        } catch (e: Exception) {

            e.printStackTrace()
            NetworkResult.Error(
                message = e.message.toString()
            )
        }

    }

    suspend fun fetchUserProfile(userId: String): NetworkResult<Profile> {
        return try {
            val response = supabase.from("profiles")
                .select {
                    filter {
                        Profile::authId eq userId
                        //or
                        eq("auth_id", userId)
                    }
                }
                .decodeSingleOrNull<Profile>()

            Log.d(TAG, "Profile fetched from supabase:: $response")
            if(response != null){
                NetworkResult.Success(response)
            }else{
                NetworkResult.Error(
                    message = "No profile found"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message.toString()
            )
        }
    }

    suspend fun checkSignedInStatus(): Boolean {

        val signInStatus = context.reloadSession(supabase)
        Log.d(TAG, "User is logged in: $signInStatus")

        return signInStatus

    }
    // TODO: Do not deploy to playstore until we have revisited https://chatgpt.com/share/67ac894b-7928-8000-a399-13809feeb1f0

    /**
     * UPDATE
     */

    /**
     * DELETE
     */


    companion object {
        private const val TAG = "ProfileNetworkClass"
    }

}

suspend fun Context.reloadSession(
    supabase: SupabaseClient
): Boolean {

    try {
        val sessionStatus = supabase.auth.sessionStatus
            .first { it !is SessionStatus.Initializing }

        return sessionStatus is SessionStatus.Authenticated

    } catch (e: Exception) {
        Log.e("Context", "Error reloading session", e)
        tokenDatastore.updateData { TokenData() }
        profileDataStore.updateData { Profile() }
        return false
    }

}
