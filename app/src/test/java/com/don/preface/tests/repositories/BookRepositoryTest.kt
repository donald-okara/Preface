package com.don.preface.tests.repositories

import com.don.preface.data.repositoryImpl.BooksRepositoryImpl
import com.don.preface.fake.data.FakeBookDetailsDataSource
import com.don.preface.fake.data.FakeBooksDataSource
import com.don.preface.fake.network.FakeBookApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BooksRepositoryTest {
    private val apiService = FakeBookApiService()
    val repository = BooksRepositoryImpl(apiService)

    @Test
    fun `searchBooks returns a list of books`() = runTest {
        //Given
        val testQuery ="testQuery"

        //When
        val response = repository.searchBooks(testQuery)

        assertEquals(FakeBooksDataSource.fakeBookList , response)
    }

    @Test
    fun `getBookDetails returns a book details`() = runTest {
        //Given
        val testBookId = "testBookId"

        //When
        val response = repository.getBookDetails(testBookId)

        //Then
        assertEquals(FakeBookDetailsDataSource.fakeBookDetails , response)
    }



}



