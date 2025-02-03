package ke.don.common_datasource.remote.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
        supabaseClient: SupabaseClient
    ): ProfileNetworkClass = ProfileNetworkClass(
        supabaseClient = supabaseClient
    )

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileNetworkClass: ProfileNetworkClass
    ): ProfileRepository = ProfileRepositoryImpl(
        profileNetworkClass = profileNetworkClass
    )

}