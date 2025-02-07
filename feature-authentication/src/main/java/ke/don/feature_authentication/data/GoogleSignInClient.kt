package ke.don.feature_authentication.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.BuildConfig
import ke.don.shared_domain.values.MAX_RETRIES
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GoogleSignInClient(
    private val context: Context,
    private val profileRepository: ProfileRepository
) {
    private val _isSignInSuccessful = MutableStateFlow(false)
    val isSignInSuccessful: StateFlow<Boolean> = _isSignInSuccessful


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



    suspend fun signInWithGoogle() {
        val maxRetries = MAX_RETRIES
        var attempt = 0

        while (attempt < maxRetries) {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val displayName = googleIdTokenCredential.displayName
                val profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()

                if (displayName != null && profilePictureUri != null) {
                    profileRepository.signInAndInsertProfile(googleIdToken, displayName, profilePictureUri)
                }

                _isSignInSuccessful.value = true
                Toast.makeText(context, "Welcome to Preface", Toast.LENGTH_SHORT).show()
                return  // Exit the function after successful sign-in

            } catch (e: Exception) {
                Log.d("GoogleSignInClient", "Attempt ${attempt + 1} failed: ${e.message}")
                attempt++

                if (attempt < maxRetries) {
                    delay(2000) // Wait 2 seconds before retrying
                } else {
                    Log.d("GoogleSignInClient", "Max retries reached. Sign in failed.")
                    Toast.makeText(context, "Sign in failed. Please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}