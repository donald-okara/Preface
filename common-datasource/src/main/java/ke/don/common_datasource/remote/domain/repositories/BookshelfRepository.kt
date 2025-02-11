package ke.don.common_datasource.remote.domain.repositories

import ke.don.shared_domain.data_models.SupabaseBookshelf

interface BookshelfRepository {
    suspend fun createBookshelf(bookshelf: SupabaseBookshelf)
    suspend fun fetchUserBookshelves(userId: String): List<SupabaseBookshelf>
    suspend fun fetchBookshelfById(bookshelfId: Int): SupabaseBookshelf?

}