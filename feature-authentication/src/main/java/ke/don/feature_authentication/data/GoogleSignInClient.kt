package ke.don.feature_authentication.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.BuildConfig
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.values.MAX_RETRIES
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class GoogleSignInClient(
    private val context: Context,
    private val profileRepository: ProfileRepository
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(): NetworkResult<NoDataReturned> {
        return try {
            repeat(3) { attempt ->
                try {
                    //val (rawNonce, hashedNonce) = profileRepository.generateNonce()

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    val credential = result.credential
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    Log.d("GoogleSignin", "Id token : $googleIdToken")
                    Log.d("GoogleSignin", "credentials : $googleIdTokenCredential")

                    val displayName = googleIdTokenCredential.displayName
                    val profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()

                    val signInSuccess = profileRepository.signInAndInsertProfile(
                        googleIdToken, displayName, profilePictureUri
                    )
                    return signInSuccess.also {
                        if (signInSuccess == NetworkResult.Success(NoDataReturned())) {
                            Toast.makeText(context, "Welcome to Preface", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: GetCredentialException) {
                    if (attempt == 1) throw e // Rethrow on last attempt
                    delay(500) // Wait 500ms before retrying
                }
            }
            NetworkResult.Error(message = "Failed after retries")
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignInClient", "Credential error: ${e.type}", e)
            Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            NetworkResult.Error(message = e.message ?: "Credential error")
        } catch (e: CancellationException){
            Log.e("GoogleSignInClient", "Cancellation error", e)
            Toast.makeText(context, "Authentication cancelled", Toast.LENGTH_SHORT).show()
            NetworkResult.Error(message = e.message ?: "Cancellation error")
        }catch (e: Exception) {
            Log.e("GoogleSignInClient", "Unknown error", e)
            Toast.makeText(context, "Sign in failed. Please try again later", Toast.LENGTH_SHORT).show()
            NetworkResult.Error(message = e.message ?: "Unknown error")
        }
    }
}