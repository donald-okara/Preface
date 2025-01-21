package com.don.preface.data.di

import android.content.Context
import com.don.preface.R
import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.domain.logger.Logger
import com.don.preface.domain.repositories.BooksRepository
import com.don.preface.domain.utils.color_utils.DefaultColorPaletteExtractor
import com.don.preface.network.GoogleBooksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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