package ke.don.common_datasource.local.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.don.shared_domain.data_models.SupabaseBook

@Entity(tableName = "bookshelves")
data class BookshelfEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val name : String = "",
    val description : String = "",
    val bookshelfType : String = "",
    val userId : String = "",
    val books : List<SupabaseBook> = emptyList()
)