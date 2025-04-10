package ke.don.common_datasource.local.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.data_models.BookshelfRef
import ke.don.shared_domain.data_models.SupabaseBook
import kotlinx.serialization.Serializable

@Serializable
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

fun BookShelf.toEntity(): BookshelfEntity {
    return BookshelfEntity(
        id = this.supabaseBookShelf.id,
        name = this.supabaseBookShelf.name,
        description = this.supabaseBookShelf.description,
        bookshelfType = this.supabaseBookShelf.bookshelfType,
        userId = this.supabaseBookShelf.userId,
        books = this.books
    )
}


fun BookshelfEntity.toBookshelf(): BookShelf {
    return BookShelf(
        supabaseBookShelf = BookshelfRef(
            id = this.id,
            name = this.name,
            description = this.description,
            bookshelfType = this.bookshelfType
        ),
        books = this.books
    )
}