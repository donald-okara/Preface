package ke.don.common_datasource.local.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ke.don.common_datasource.local.roomdb.dao.BookshelfDao
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity

@Database(entities = [BookshelfEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  // <-- Add this
abstract class BookshelfDatabase: RoomDatabase() {
    abstract fun bookshelfDao(): BookshelfDao

    companion object {
        @Volatile
        private var Instance : BookshelfDatabase? = null

        fun getDatabase(context: Context): BookshelfDatabase{

            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context,
                    BookshelfDatabase::class.java,
                    "bookshelf_database"
                ).build()
                    .also { Instance = it }
            }
        }
    }

}