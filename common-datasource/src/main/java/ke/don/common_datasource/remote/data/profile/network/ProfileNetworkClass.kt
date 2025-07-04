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
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.ktor.client.call.body
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ke.don.common_datasource.local.datastore.profile.profileDataStore
import ke.don.common_datasource.local.datastore.token.TokenData
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import ke.don.common_datasource.local.datastore.token.tokenDatastore
import ke.don.common_datasource.remote.domain.error_handler.CompositeErrorHandler
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfCatalog
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull


class ProfileNetworkClass(
    private val context: Context,
    private val supabase: SupabaseClient,
    private val tokenDatastore: TokenDatastoreManager
) {
    private val errorHandler = CompositeErrorHandler()

    /**
     * CREATE
     */
    suspend fun signIn(
        idToken: String,
    ): NetworkResult<UserInfo>{
        return try {
            supabase.auth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
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
            errorHandler.handleException(e)
        }

    }

    suspend fun insertUserProfile(
        profile : Profile
    ): NetworkResult<Profile>{
        return try {
            val response= supabase.from(PROFILESTABLE)
                .insert(profile) { select() }
                .decodeSingleOrNull<Profile>()
            Log.d(TAG, "Profile inserted successfully")

            if (response != null) {
                NetworkResult.Success(response)
            } else {
                NetworkResult.Error(message = "Failed to create profile")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
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
            errorHandler.handleException(e)

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
            errorHandler.handleException(e)
        }
    }

    suspend fun fetchProfileDetails(userId: String): NetworkResult<ProfileDetails?> {
        return try {
            val response = supabase.from("profile_discovered_books")
                .select {
                    filter {
                        ProfileDetails::authId eq userId
                        //or
                        eq("auth_id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileDetails>()

            Log.d(TAG, "Profile fetched from supabase:: $response")
            NetworkResult.Success(response)
        }catch (e: Exception){
            errorHandler.handleException(e)
        }
    }

    suspend fun checkSignedInStatus(): Boolean {
        val signInStatus = context.reloadSession()
        Log.d(TAG, "User is logged in: $signInStatus")

        return signInStatus

    }

    /**
     * UPDATE
     */

    /**
     * DELETE
     */
    suspend fun signOut(): NetworkResult<NoDataReturned> {
        return try {
            supabase.auth.signOut()

            NetworkResult.Success(NoDataReturned())
        }catch (e:Exception){
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    suspend fun deleteProfile(userId: String): NetworkResult<NoDataReturned> {
        return try {
            supabase.from("profiles").update(
                mapOf("delete_account_request" to true)
            ){
                filter {
                    Profile::authId eq userId
                }
            }
            supabase.auth.signOut()

            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception){
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    companion object {
        private const val TAG = "ProfileNetworkClass"
    }

}

suspend fun Context.reloadSession(): Boolean {
    try {
        val localProfile =  withTimeoutOrNull(5_000) { // 5-second timeout
            profileDataStore.data
                .filter { it.authId.isNotEmpty() }
                .first()
        } ?: Profile(authId = "", /* other default fields */) // or throw an exception

        Log.e("Context", "profile: $localProfile",)

        return (localProfile.authId.isNotEmpty())

    } catch (e: Exception) {
        Log.e("Context", "Error reloading session", e)
        tokenDatastore.updateData { TokenData() }
        profileDataStore.updateData { Profile() }
        return false
    }

}
