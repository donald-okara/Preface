package ke.don.common_datasource.remote.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.data.profile.repositoryImpl.ProfileRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.Profile
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatasourceModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient = SupabaseClientProvider.supabase

    @Provides
    @Singleton
    fun provideUserProfile(profileNetworkClass: ProfileNetworkClass): Profile? = runBlocking {
        profileNetworkClass.fetchUserProfile()
    }


    @Provides
    @Singleton
    fun provideProfileNetworkClass(
        supabaseClient: SupabaseClient,
        tokenDatastoreManager: TokenDatastoreManager,
        @ApplicationContext context : Context
    ): ProfileNetworkClass = ProfileNetworkClass(
        supabase = supabaseClient,
        tokenDatastore = tokenDatastoreManager,
        context = context
    )

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileNetworkClass: ProfileNetworkClass,
        profileDataStoreManager: ProfileDataStoreManager,
        @ApplicationContext context: Context,
        userProfile: Profile?
    ): ProfileRepository = ProfileRepositoryImpl(
        profileNetworkClass = profileNetworkClass,
        profileDataStoreManager = profileDataStoreManager,
        context = context,
        profile = userProfile
    )

    @Provides
    @Singleton
    fun provideBookshelfNetworkClass(
        supabaseClient: SupabaseClient,
    ): BookshelfNetworkClass  = BookshelfNetworkClass(supabaseClient)

    @Provides
    @Singleton
    fun provideBookshelfRepository(
        bookshelfNetworkClass: BookshelfNetworkClass,
        @ApplicationContext context: Context,
        profileRepository: ProfileRepository
    ): BookshelfRepository = BookshelfRepositoryImpl(
        bookshelfNetworkClass = bookshelfNetworkClass,
        context = context,
        profileRepository = profileRepository
    )

}