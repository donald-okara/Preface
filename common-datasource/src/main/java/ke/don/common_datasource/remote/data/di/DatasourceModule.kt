package ke.don.common_datasource.remote.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.common_datasource.local.datastore.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.TokenDatastoreManager
import ke.don.common_datasource.remote.data.network.ProfileNetworkClass
import ke.don.common_datasource.remote.data.repositoryImpl.ProfileRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = SupabaseClient

    @Provides
    @Singleton
    fun provideProfileNetworkClass(
        supabaseClient: SupabaseClient,
        tokenDatastoreManager: TokenDatastoreManager,
        @ApplicationContext context : Context
    ): ProfileNetworkClass = ProfileNetworkClass(
        supabaseClient = supabaseClient,
        tokenDatastore = tokenDatastoreManager,
        context = context
    )

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileNetworkClass: ProfileNetworkClass,
        profileDataStoreManager: ProfileDataStoreManager
    ): ProfileRepository = ProfileRepositoryImpl(
        profileNetworkClass = profileNetworkClass,
        profileDataStoreManager = profileDataStoreManager
    )

}