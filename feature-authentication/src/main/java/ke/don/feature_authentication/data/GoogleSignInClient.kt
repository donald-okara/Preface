package ke.don.feature_authentication.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.BuildConfig
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.values.MAX_RETRIES
import kotlinx.coroutines.delay

class GoogleSignInClient(
    private val context: Context,
    private val profileRepository: ProfileRepository
) {
    private val credentialManager = CredentialManager.create(context)


    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption
        .Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
        .setNonce(profileRepository.hashedNonce)
        .build()

    private val request : GetCredentialRequest = GetCredentialRequest
        .Builder()
        .addCredentialOption(googleIdOption)
        .build()


    suspend fun signInWithGoogle(): NetworkResult<NoDataReturned> {
            return try {
                val result = credentialManager.getCredential(
                    request = request, context = context
                )
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val displayName = googleIdTokenCredential.displayName
                val profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                if (displayName == null || profilePictureUri == null) {
                    Log.d("GoogleSignInClient", "Display name:: $displayName, profilePictureUri:: $profilePictureUri")
                    NetworkResult.Error(message = "Null name or profile url")
                }else{
                    Log.d("GoogleSignInClient", "Display name:: $displayName, profilePictureUri:: $profilePictureUri")
                    val signInSuccess = profileRepository.signInAndInsertProfile(
                        googleIdToken, displayName, profilePictureUri
                    )
                    signInSuccess.also {
                        if (signInSuccess == NetworkResult.Success(NoDataReturned())) {
                            Toast.makeText(context, "Welcome to Preface", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context, "Sign in failed. Please try again later", Toast.LENGTH_SHORT
                ).show()
                return NetworkResult.Error(message = e.message ?: "Unknown error")
            }
        }

}