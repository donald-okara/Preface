package ke.don.common_datasource.remote.data.book_details.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import ke.don.common_datasource.local.datastore.profile.ProfileDataStoreManager
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.remote.data.book_details.network.BookNetworkClass
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
import ke.don.common_datasource.remote.data.book_details.repositoryImpl.BooksRepositoryImpl
import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.common_datasource.remote.domain.usecases.BooksUseCasesImpl
import ke.don.shared_domain.BuildConfig
import ke.don.shared_domain.utils.color_utils.ColorPaletteExtractor
import ke.don.shared_domain.utils.color_utils.DefaultColorPaletteExtractor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBookNetworkClass(supabaseClient: SupabaseClient): BookNetworkClass = BookNetworkClass(supabaseClient)

    @Provides
    @Singleton
    fun provideBooksRepository(
        googleBooksApi: GoogleBooksApi,
        bookshelfNetworkClass: BookshelfNetworkClass,
        bookshelfDao: BookshelfDao,
        bookNetworkClass: BookNetworkClass,
        bookshelfRepository: BookshelfRepository,
        @ApplicationContext context: Context,
    ): BooksRepository {
        return BooksRepositoryImpl(
            googleBooksApi = googleBooksApi,
            apiKey = BuildConfig.GOOGLE_API_KEY,
            bookshelfNetworkClass = bookshelfNetworkClass,
            bookshelfDao = bookshelfDao,
            context = context,
            bookNetworkClass = bookNetworkClass,
            bookshelfRepository = bookshelfRepository,
        )
    }

    @Provides
    @Singleton
    fun providesColorPaletteExtractor(): ColorPaletteExtractor = DefaultColorPaletteExtractor()


    @Provides
    @Singleton
    fun provideBooksUseCases(
        booksRepository: BooksRepository,
        progressRepository: UserProgressRepository,
        profileDataStoreManager: ProfileDataStoreManager
    ): BooksUseCases {
        return BooksUseCasesImpl(
            booksRepository = booksRepository,
            progressRepository = progressRepository,
            profileDataStoreManager = profileDataStoreManager
        )
    }

}