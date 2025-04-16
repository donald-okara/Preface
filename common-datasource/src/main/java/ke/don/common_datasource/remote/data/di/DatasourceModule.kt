package ke.don.common_datasource.remote.data.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.datastore.token.TokenDatastoreManager
import ke.don.common_datasource.local.roomdb.BookshelfDatabase
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.data.bookshelf.repositoryImpl.BookshelfRepositoryImpl
import ke.don.common_datasource.remote.data.profile.network.ProfileNetworkClass
import ke.don.common_datasource.remote.data.profile.repositoryImpl.ProfileRepositoryImpl
import ke.don.common_datasource.remote.data.user_progress.network.UserProgressNetworkClass
import ke.don.common_datasource.remote.data.user_progress.repositoryImpl.UserProgressRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCaseClass
import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCases
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
    fun provideUserProfile(profileDataStoreManager: ProfileDataStoreManager): Profile = runBlocking {
        profileDataStoreManager.getProfileFromDatastore()
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
        bookshelfDao: BookshelfDao,
    ): ProfileRepository = ProfileRepositoryImpl(
        profileNetworkClass = profileNetworkClass,
        bookshelfDao = bookshelfDao,
        profileDataStoreManager = profileDataStoreManager,
        context = context,
    )

    @Provides
    @Singleton
    fun provideBookshelfNetworkClass(
        supabaseClient: SupabaseClient,
    ): BookshelfNetworkClass  = BookshelfNetworkClass(supabaseClient)

    @Provides
    @Singleton
    fun provideBookshelfDatabase(@ApplicationContext context: Context): BookshelfDatabase {
        return Room.databaseBuilder(
            context,
            BookshelfDatabase::class.java,
            "bookshelf_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookshelfDao(database: BookshelfDatabase): BookshelfDao = database.bookshelfDao()

    @Provides
    @Singleton
    fun provideBookshelfRepository(
        bookshelfNetworkClass: BookshelfNetworkClass,
        @ApplicationContext context: Context,
        userProfile: Profile?,
        bookshelfDao: BookshelfDao
    ): BookshelfRepository = BookshelfRepositoryImpl(
        bookshelfNetworkClass = bookshelfNetworkClass,
        context = context,
        userProfile = userProfile,
        bookshelfDao = bookshelfDao
    )

    @Provides
    @Singleton
    fun provideUserProgressNetworkClass(
        supabaseClient: SupabaseClient
    ): UserProgressNetworkClass = UserProgressNetworkClass(supabaseClient)

    @Provides
    @Singleton
    fun provideUserProgressRepository(
        @ApplicationContext context: Context,
        userProgressNetworkClass: UserProgressNetworkClass
    ) : UserProgressRepository = UserProgressRepositoryImpl(
        context = context,
        userProgressNetworkClass = userProgressNetworkClass
    )

    @Provides
    @Singleton
    fun provideProfileUseCases(
        profileRepository: ProfileRepository
    ): ProfileTabUseCases = ProfileTabUseCaseClass(profileRepository)
}