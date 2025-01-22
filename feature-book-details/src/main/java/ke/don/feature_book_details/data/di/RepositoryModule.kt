package ke.don.feature_book_details.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.don.feature_book_details.R
import ke.don.feature_book_details.data.repositoryImpl.BooksRepositoryImpl
import ke.don.feature_book_details.domain.logger.Logger
import ke.don.feature_book_details.domain.repositories.BooksRepository
import ke.don.feature_book_details.domain.utils.color_utils.DefaultColorPaletteExtractor
import ke.don.feature_book_details.network.GoogleBooksApi
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
            apiKey = context.getString(R.string.api_key),
            logger = logger,
            colorPaletteExtractor = DefaultColorPaletteExtractor()
        )
    }
}