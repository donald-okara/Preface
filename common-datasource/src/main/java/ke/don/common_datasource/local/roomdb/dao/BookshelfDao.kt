package ke.don.common_datasource.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import kotlinx.coroutines.flow.Flow

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
    fun getAllBookshelves(): List<BookshelfEntity>


    @Query("SELECT * FROM bookshelves WHERE id = :id")
    fun getBookshelfById(id: Int): Flow<BookshelfEntity>

    /**
     * UPDATE
     */
    @Update
    suspend fun update(bookshelfEntity: BookshelfEntity)

    /**
     * DELETE
     */
    @Query("DELETE FROM bookshelves WHERE id = :id")
    suspend fun deleteBookshelfById(id: Int)

    @Query("DELETE FROM bookshelves")
    suspend fun deleteAllBookshelves()
}