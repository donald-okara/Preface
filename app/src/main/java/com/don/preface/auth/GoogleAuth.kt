package com.don.preface.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.don.preface.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class GoogleAuth(
    private val context: Context,
    val activityResultLauncher: ActivityResultLauncher<Intent>,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val googleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.client_id)) // Add client ID in strings.xml
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/books"))
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleAccountToken(account: GoogleSignInAccount): String? {
        return account.serverAuthCode
    }

    fun startSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    fun fetchGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }


    fun handleSignInResult(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign-in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    addUserToFirestore(account)  // Add this line
                } else {
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun addUserToFirestore(account: GoogleSignInAccount) {
        // Prepare user data to store in Firestore
        val userData = mapOf(
            "userId" to firebaseAuth.currentUser?.uid,
            "displayName" to account.displayName,
            "email" to account.email,
            "profilePictureUrl" to account.photoUrl.toString()
        )

        // Add user to Firestore under 'users' collection
        firebaseAuth.currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User added to Firestore successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add user to Firestore", e)
                }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    companion object {
        private const val TAG = "GoogleSignInHelper"
    }
}