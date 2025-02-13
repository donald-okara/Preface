package ke.don.common_datasource.remote.data.book_details.data.di

import ke.don.shared_domain.logger.Logger
import ke.don.shared_domain.logger.LoggerImpl
import ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi
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
    fun providesLogger(): Logger {
        return LoggerImpl()
    }

}
