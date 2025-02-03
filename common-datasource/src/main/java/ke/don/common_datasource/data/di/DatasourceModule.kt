package ke.don.common_datasource.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.don.common_datasource.data.repositoryImpl.ProfileRepositoryImpl
import ke.don.common_datasource.domain.repositories.ProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {
    @Provides
    @Singleton
    fun provideUserManager(): UserManager = UserManager()

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = SupabaseClient

    @Provides
    @Singleton
    fun provideProfileRepository(
        supabaseClient: SupabaseClient,
        userManager: UserManager
    ): ProfileRepository = ProfileRepositoryImpl(
        supabaseClient = supabaseClient,
        userProfile = userManager
    )

}