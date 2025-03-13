package ke.don.common_datasource.local.roomdb

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ke.don.shared_domain.data_models.SupabaseBook

class Converters {
    @TypeConverter
    fun fromBooksList(books: List<SupabaseBook>): String {
        return Json.encodeToString(books)
    }

    @TypeConverter
    fun toBooksList(data: String): List<SupabaseBook> {
        return try {
            Json.decodeFromString(data)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
