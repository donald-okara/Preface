package ke.don.common_datasource.remote.data.book_details.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.common_datasource.remote.data.book_details.repositoryImpl.BooksRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.BuildConfig
import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.utils.color_utils.DefaultColorPaletteExtractor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBooksRepository(
        googleBooksApi: GoogleBooksApi,
        bookshelfRepository: BookshelfRepository,
        @ApplicationContext context: Context,
        logger : Logger
    ): BooksRepository {
        return BooksRepositoryImpl(
            googleBooksApi = googleBooksApi,
            apiKey = BuildConfig.GOOGLE_API_KEY,
            logger = logger,
            bookshelfRepository = bookshelfRepository,
            colorPaletteExtractor = DefaultColorPaletteExtractor()
        )
    }

    @Provides
    @Singleton
    fun provideBooksUseCases(
        booksRepository: BooksRepository,
        bookshelfRepository: BookshelfRepository
    ): BooksUseCases {
        return BooksUseCases(
            booksRepository = booksRepository,
            bookshelfRepository = bookshelfRepository
        )
    }

}