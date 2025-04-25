package ke.don.feature_bookshelf.fake

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.common_datasource.local.roomdb.entities.toEntity
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.feature_bookshelf.fake.FakeBookshelf.fakeBookShelves
import ke.don.feature_bookshelf.fake.FakeBookshelf.fictionBookshelf
import ke.don.feature_bookshelf.fake.FakeBookshelf.scienceBookshelfRef
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.states.NetworkResult
import kotlinx.coroutines.flow.Flow

class FakeBookshelfRepository() : BookshelfRepository{
    override suspend fun createBookshelf(bookshelf: BookshelfRef): NetworkResult<BookshelfRef> {
        return if (bookshelf == scienceBookshelfRef){
             NetworkResult.Success(scienceBookshelfRef)
        }else{
            NetworkResult.Error(message = "Something went wrong")
        }
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): NetworkResult<BookshelfEntity> {
        return if (bookshelfId != -1) {
            NetworkResult.Success(fictionBookshelf.toEntity())
        }else {
            NetworkResult.Error(message = "Something went wrong")
        }
    }

    override suspend fun fetchUserBookShelves(): NetworkResult<List<BookShelf>> {
        return NetworkResult.Success(fakeBookShelves)
    }

    override suspend fun syncLocalBookshelvesDb(): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun editBookshelf(
        bookshelfId: Int,
        bookshelf: BookshelfRef
    ): NetworkResult<NoDataReturned> {
        return if (bookshelfId != -1){
            NetworkResult.Success(NoDataReturned())
        }else{
            NetworkResult.Error(message = "Something went wrong")
        }
    }

    override suspend fun fetchBookshelfRef(bookshelfId: Int): NetworkResult<BookshelfRef> {
        return NetworkResult.Success(scienceBookshelfRef)
    }

    override suspend fun addBookToBookshelf(
        bookshelfId: Int,
        bookId: String
    ): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun removeBookFromBookshelf(
        bookId: String,
        bookshelfId: Int
    ): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBookshelf(bookshelfId: Int): NetworkResult<NoDataReturned> {
        return if (bookshelfId == -1){
             NetworkResult.Error(message = "Invalid bookshelfId")
        }else{
            NetworkResult.Success(NoDataReturned())

        }
    }

}