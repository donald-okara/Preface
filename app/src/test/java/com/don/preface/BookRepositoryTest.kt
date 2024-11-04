package com.don.preface

import com.don.preface.data.model.BookResponse
import com.don.preface.data.model.VolumeData
import com.don.preface.network.GoogleBooksApi
import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.fake.createFakeVolumeData
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

class BooksRepositoryTest {

    private lateinit var repository: BooksRepositoryImpl

    @Mock
    private lateinit var googleBooksApi: GoogleBooksApi

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = BooksRepositoryImpl(googleBooksApi)
    }

    @Test
    fun `searchBooks returns success`() = runBlocking {
        // Mock response
        val mockResponse = BookResponse("books#volumes", 1, listOf())
        Mockito.`when`(googleBooksApi.searchBooks("Harry Potter"))
            .thenReturn(Response.success(mockResponse))

        // Call repository
        val result = repository.searchBooks("Harry Potter")

        // Assert result
        assertTrue(result.isSuccessful)
        assertEquals(result.body()?.kind, "books#volumes")
    }

    @Test
    fun `searchBooks returns error`() = runBlocking {
        // Mock error response
        val errorResponse = Response.error<BookResponse>(404, ResponseBody.create(null, ""))
        Mockito.`when`(googleBooksApi.searchBooks("Unknown Book"))
            .thenReturn(errorResponse)

        // Call repository
        val result = repository.searchBooks("Unknown Book")

        // Assert result
        assertFalse(result.isSuccessful)
        assertEquals(result.code(), 404)
    }

    @Test
    fun `getBookDetails returns VolumeData`() = runBlocking {
        val mockResponse = createFakeVolumeData()
        Mockito.`when`(repository.getBookDetails("5cu7sER89nwC"))
            .thenReturn(Response.success(mockResponse))

        val result = repository.getBookDetails("5cu7sER89nwC")

        assertTrue(result.isSuccessful)
        assertEquals(result.body(), mockResponse)
    }


}
