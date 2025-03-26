package ke.don.feature_authentication.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SignInModule {

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        profileRepository: ProfileRepository
    ): GoogleSignInClient {
        return GoogleSignInClient(
            context = context,
            profileRepository = profileRepository
        )
    }

}