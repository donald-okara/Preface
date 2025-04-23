package ke.don.common_datasource.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.data_models.SupabaseBook
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Dao
interface BookshelfDao {
    /**
     * CREATE
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookshelfEntity: BookshelfEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookshelfEntities: List<BookshelfEntity>)

    /**
     * READ
     */
    @Query("SELECT * FROM bookshelves")
    fun getAllBookshelvesFlow(): Flow<List<BookshelfEntity>>

    @Query("SELECT * FROM bookshelves")
    suspend fun getAllBookshelves(): List<BookshelfEntity>


    @Query("SELECT * FROM bookshelves WHERE id = :id")
    fun getBookshelfById(id: Int): BookshelfEntity

    /**
     * UPDATE
     */
    @Update
    suspend fun updateBookshelf(bookshelfEntity: BookshelfEntity)

    @Query("UPDATE bookshelves SET books = :updatedBooks WHERE id = :bookshelfId")
    suspend fun updateBooksInBookshelf(bookshelfId: Int, updatedBooks: String)

    suspend fun addBookToBookshelf(bookshelfId: Int, book: SupabaseBook) {
        val bookshelf = getAllBookshelves().find { it.id == bookshelfId }
            ?: throw IllegalArgumentException("Bookshelf with ID $bookshelfId not found")
        val updatedBooks = bookshelf.books.toMutableList().apply { add(book) }.distinctBy { it.bookId }
        val updatedBooksJson = Json.encodeToString(updatedBooks)
        updateBooksInBookshelf(bookshelfId, updatedBooksJson)
    }

    suspend fun removeBookFromBookshelf(bookshelfId: Int, bookId: String) {
        val bookshelf = getAllBookshelves().find { it.id == bookshelfId }
            ?: throw IllegalArgumentException("Bookshelf with ID $bookshelfId not found")
        val updatedBooks = bookshelf.books.filter { it.bookId != bookId }
        val updatedBooksJson = Json.encodeToString(updatedBooks)
        updateBooksInBookshelf(bookshelfId, updatedBooksJson)
    }
    /**
     * DELETE
     */
    @Query("DELETE FROM bookshelves WHERE id = :id")
    suspend fun deleteBookshelfById(id: Int)


    @Query("DELETE FROM bookshelves WHERE id NOT IN (:validIds)")
    suspend fun deleteBookshelvesNotIn(validIds: Set<Int>)

    @Query("DELETE FROM bookshelves")
    suspend fun deleteAllBookshelves()
}