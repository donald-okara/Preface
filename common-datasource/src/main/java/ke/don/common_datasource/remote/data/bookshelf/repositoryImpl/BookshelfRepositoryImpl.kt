package ke.don.common_datasource.remote.data.bookshelf.repositoryImpl

import ke.don.common_datasource.remote.data.bookshelf.network.BookshelfNetworkClass
import ke.don.common_datasource.remote.domain.repositories.BookshelfRepository
import ke.don.shared_domain.data_models.SupabaseBookshelf

class BookshelfRepositoryImpl(
    private val bookshelfNetworkClass: BookshelfNetworkClass
): BookshelfRepository {
    override suspend fun createBookshelf(bookshelf: SupabaseBookshelf) {
        bookshelfNetworkClass.createBookshelf(bookshelf)
    }

    override suspend fun fetchUserBookshelves(userId: String): List<SupabaseBookshelf> {
        return bookshelfNetworkClass.fetchUserBookshelves(userId)
    }

    override suspend fun fetchBookshelfById(bookshelfId: Int): SupabaseBookshelf? {
        return bookshelfNetworkClass.fetchBookshelfById(bookshelfId)
    }
}