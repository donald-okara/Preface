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
import ke.don.common_datasource.domain.repositories.ProfileRepository
import ke.don.shared_domain.BuildConfig

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



    suspend fun signInWithGoogle(){
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            // Extract necessary data from GoogleIdTokenCredential
            val displayName = googleIdTokenCredential.displayName
            val profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()

            profileRepository.signInAndCheckProfile(googleIdToken, displayName, profilePictureUri)


            Log.d("GoogleIDToken", googleIdToken)
            Toast.makeText(context, "Google ID Token: $googleIdToken", Toast.LENGTH_SHORT).show()
        }  catch (e: GetCredentialException) {
            Log.d("GetCredentialException", e.toString())
            Toast.makeText(context, "Sign in failed credential exception", Toast.LENGTH_SHORT).show()
        } catch (e: GoogleIdTokenParsingException){
            Log.d("GetCredentialException", e.toString())
            Toast.makeText(context, "Sign in failed google id token parsing exception", Toast.LENGTH_SHORT).show()
        }catch (e: Exception) {
            Log.d("SupabaseException", e.toString())
            Toast.makeText(context, "Sign in failed with Supabase", Toast.LENGTH_SHORT).show()
        }

    }

}