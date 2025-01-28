package ke.don.feature_book_details.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.feature_book_details.data.repositoryImpl.BooksRepositoryImpl
import ke.don.feature_book_details.domain.repositories.BooksRepository
import ke.don.feature_book_details.domain.usecase.BooksUseCases
import ke.don.feature_book_details.network.GoogleBooksApi
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
        @ApplicationContext context: Context,
        logger : Logger
    ): BooksRepository {
        return BooksRepositoryImpl(
            googleBooksApi = googleBooksApi,
            apiKey = BuildConfig.GOOGLE_API_KEY,
            logger = logger,
            colorPaletteExtractor = DefaultColorPaletteExtractor()
        )
    }

    @Provides
    @Singleton
    fun provideBooksUseCases(
        booksRepository: BooksRepository
    ): BooksUseCases {
        return BooksUseCases(booksRepository)
    }

}