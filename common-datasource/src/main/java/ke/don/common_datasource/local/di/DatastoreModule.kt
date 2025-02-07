package ke.don.common_datasource.local.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {
    @Provides
    @Singleton
    fun provideProfileDataStoreManager(
        @ApplicationContext context: Context
    ): ProfileDataStoreManager = ProfileDataStoreManager(context)

    @Provides
    @Singleton
    fun provideTokenDataStoreManager(
        @ApplicationContext context: Context
    ): TokenDatastoreManager = TokenDatastoreManager(context)

}