package com.don.preface.domain

import com.don.preface.R
import com.don.preface.data.repositories.BooksRepository
import com.don.preface.data.repositories.UserRepository
import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.data.repositoryImpl.UserRepositoryImpl
import com.don.preface.network.GoogleBooksApi
import com.don.preface.domain.logger.Logger
import com.don.preface.domain.logger.LoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val BASE_URL = "https://www.googleapis.com/books/v1/"

    @Provides
    @Singleton
    @Named("apiKey")
    fun provideApiKey(): String {
        return R.string.api_key.toString()
    }

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
    fun provideBooksRepository(googleBooksApi: GoogleBooksApi): BooksRepository {
        return BooksRepositoryImpl(googleBooksApi)
    }

    @Provides
    @Singleton
    fun providesUserRepository(
        googleBooksApi: GoogleBooksApi,
        @Named("googleAccountToken") accessToken: String,
        @Named("apiKey") apiKey: String
    ): UserRepository {
        return UserRepositoryImpl(
            googleBooksApi = googleBooksApi,
            accessToken = accessToken,
            apiKey = apiKey
        )
    }

    @Provides
    @Singleton
    fun providesLogger(): Logger {
        return LoggerImpl()
    }

}
