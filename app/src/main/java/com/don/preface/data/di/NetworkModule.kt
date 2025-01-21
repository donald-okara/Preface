package com.don.preface.data.di

import android.content.Context
import com.don.preface.R
import com.don.preface.domain.repositories.BooksRepository
import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.network.GoogleBooksApi
import com.don.preface.domain.logger.Logger
import com.don.preface.domain.logger.LoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val BASE_URL = "https://www.googleapis.com/books/v1/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApi(retrofit: Retrofit): GoogleBooksApi {
        return retrofit.create(GoogleBooksApi::class.java)
    }


    @Provides
    @Singleton
    fun providesLogger(): Logger {
        return LoggerImpl()
    }

}
