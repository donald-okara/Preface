package ke.don.feature_book_details.fake.contracts

import ke.don.common_datasource.remote.domain.states.BookshelvesState
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.common_datasource.remote.domain.usecases.BooksUseCases
import ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource.fakeBookDetailsResponse
import ke.don.feature_book_details.fake.data.FakeBookshelfState.fakeBookshelvesState
import ke.don.feature_book_details.fake.data.FakeUserProgress.fakeUserProgressState
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.states.NetworkResult

class FakeBooksUseCases(): BooksUseCases {
    override suspend fun checkAndAddBook(book: BookDetailsResponse): NetworkResult<NoDataReturned> {
        return if (book == fakeBookDetailsResponse){
            NetworkResult.Success(NoDataReturned())
        }else {
            NetworkResult.Error(message = "Something went wrong")
        }
    }

    override suspend fun addUserProgress(userProgress: CreateUserProgressDTO): NetworkResult<NoDataReturned> {
        return if (userProgress.currentPage != 0){
            NetworkResult.Success(NoDataReturned())
        }else{
            NetworkResult.Error(message = "Something went wrong in add")
        }
    }

    override suspend fun updateUserProgress(
        userId: String,
        bookId: String,
        newCurrentPage: Int
    ): NetworkResult<NoDataReturned> {
        return if (newCurrentPage != 0){
            NetworkResult.Success(NoDataReturned())
        }else{
            NetworkResult.Error(message = "Something went wrong in update")
        }
    }

    override suspend fun fetchProfileId(): String = "fakeUserId"


    override suspend fun pushEditedBookshelfBooks(
        bookId: String,
        removeBookshelfIds: List<Int>,
        addBookshelves: List<Int>
    ): NetworkResult<NoDataReturned> {
        return NetworkResult.Success(NoDataReturned())
    }

    override suspend fun fetchBookDetails(bookId: String): NetworkResult<BookDetailsResponse> {
        return if(bookId == "5cu7sER89nwC"){
            NetworkResult.Success(fakeBookDetailsResponse)
        }else{
            NetworkResult.Error(message = "Something went wrong")
        }
    }

    override suspend fun fetchAndMapBookshelves(bookId: String): BookshelvesState? {
        return fakeBookshelvesState
    }

    override suspend fun fetchBookProgress(bookId: String, userId: String): UserProgressState? {
        return if(bookId != "null" && userId != "null"){
            fakeUserProgressState
        }else{
            null
        }
    }
}