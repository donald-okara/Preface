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
import ke.don.shared_domain.BuildConfig
import java.security.MessageDigest
import java.util.UUID

class GoogleSignInClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    private val rawNonce = UUID.randomUUID().toString()
    private val bytes = rawNonce.toByteArray()
    private val md = MessageDigest.getInstance("SHA-256")
    private val digest = md.digest(bytes)
    private val nonce = digest.fold("") { str, it -> str + "%02x".format(it) }


    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption
        .Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
        .setNonce(nonce)
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

            Log.d("GoogleIDToken", googleIdToken)
            Toast.makeText(context, "Google ID Token: $googleIdToken", Toast.LENGTH_SHORT).show()
        }  catch (e: GetCredentialException) {
            Log.d("GetCredentialException", e.toString())
            Toast.makeText(context, "Sign in failed credential exception", Toast.LENGTH_SHORT).show()
        } catch (e: GoogleIdTokenParsingException){
            Log.d("GetCredentialException", e.toString())
            Toast.makeText(context, "Sign in failed google id token parsing exception", Toast.LENGTH_SHORT).show()
        }

    }

}