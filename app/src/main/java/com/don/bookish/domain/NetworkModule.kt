package com.don.bookish.domain

import com.don.bookish.data.repositories.BooksRepository
import com.don.bookish.data.repositoryImpl.BooksRepositoryImpl
import com.don.bookish.network.GoogleBooksApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideBooksRepository(googleBooksApi: GoogleBooksApi): BooksRepository {
        return BooksRepositoryImpl(googleBooksApi)
    }

}
