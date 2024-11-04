package com.don.preface.domain

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.don.preface.auth.GoogleAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleAuth(
        @ApplicationContext context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): GoogleAuth {
        return GoogleAuth(
            context,
            activityResultLauncher,
            firestore,
            firebaseAuth
        )
    }



    @Provides
    @Singleton
    @Named("googleAccountToken")
    fun provideGoogleAccountToken(
        googleAuth: GoogleAuth
    ): String? {
        val account = googleAuth.fetchGoogleAccount()

        return account?.let { googleAuth.getGoogleAccountToken(it) }
    }
}