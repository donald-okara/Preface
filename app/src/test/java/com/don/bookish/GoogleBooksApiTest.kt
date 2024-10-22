package com.don.bookish

import com.don.bookish.data.model.BookResponse
import com.don.bookish.data.model.VolumeData
import com.don.bookish.network.GoogleBooksApi
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class GoogleBooksApiTest {

    private lateinit var googleBooksApi: GoogleBooksApi

    @Before
    fun setUp() {
        // Set up Retrofit instance and the GoogleBooksApi interface
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        googleBooksApi = retrofit.create(GoogleBooksApi::class.java)
    }

    @Test
    fun `searchBooks returns successful response`() = runBlocking {
        // Given
        val query = "kotlin"
        val mockResponse = mock<BookResponse>()
        val response = Response.success(mockResponse)

        // Mock the actual API call
        val api = mock<GoogleBooksApi>()
        whenever(api.searchBooks(query)).thenReturn(response)

        // When
        val result = api.searchBooks(query)

        // Then
        assert(result.isSuccessful)
        assert(result.body() == mockResponse)
        assert(result.body() is BookResponse)
    }

    @Test
    fun `searchBooks returns error response`() = runBlocking {
        // Given
        val query = "kotlin"
        val responseBody = mock<ResponseBody>()
        val response = Response.error<BookResponse>(404, responseBody)

        // Mock the actual API call
        val api = mock<GoogleBooksApi>()
        whenever(api.searchBooks(query)).thenReturn(response)

        // When
        val result = api.searchBooks(query)

        // Then
        assert(!result.isSuccessful)
        assert(result.code() == 404)
        assert(result.errorBody() == responseBody)
    }

    @Test
    fun `getBookDetails returns successful response`() = runBlocking {
        // Given
        val bookId = "12345"
        val mockResponse = mock<VolumeData>()
        val response = Response.success(mockResponse)

        // Mock the actual API call
        val api = mock<GoogleBooksApi>()
        whenever(api.getBookDetails(bookId)).thenReturn(response)

        val result = api.getBookDetails(bookId)

        // Then
        assert(result.isSuccessful)
        assert(result.body() == mockResponse)
        assert(result.body() is VolumeData)
    }

    @Test
    fun `getBookDetails returns error response`() = runBlocking {
        // Given
        val bookId = "12345"
        val responseBody = mock<ResponseBody>()
        val response = Response.error<VolumeData>(404, responseBody)

        // Mock the actual API call
        val api = mock<GoogleBooksApi>()
        whenever(api.getBookDetails(bookId)).thenReturn(response)

        val result = api.getBookDetails(bookId)

        // Then
        assert(!result.isSuccessful)
        assert(result.code() == 404)
    }

}
