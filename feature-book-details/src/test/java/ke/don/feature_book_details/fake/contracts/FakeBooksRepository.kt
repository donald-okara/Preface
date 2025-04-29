package ke.don.feature_book_details.fake.contracts

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.feature_book_details.fake.data.FakeBooksDataSource.fakeBookListItemResponse
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.states.NetworkResult

class FakeBooksRepository: BooksRepository {
    override suspend fun checkAndAddBook(book: BookDetailsResponse): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun searchBooks(query: String): NetworkResult<List<BookItem>> {
        return if(query.isNotEmpty()) NetworkResult.Success(fakeBookListItemResponse) else NetworkResult.Error(message ="Something went wrong")
    }

    override suspend fun getBookDetails(bookId: String): NetworkResult<BookDetailsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchBookshelves(): List<BookshelfEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun pushEditedBookshelfBooks(
        bookId: String,
        bookshelfIds: List<Int>,
        addBookshelves: List<Int>
    ): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

}