package ke.don.shared_domain.data_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Serializable
data class CreateUserProgressDTO(
    @SerialName("book_id")
    val bookId: String,
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class UserProgressResponse(
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("book_id")
    val bookId: String = "",
    @SerialName("current_page")
    val currentPage: Int = 0,
    @SerialName("total_pages")
    val totalPages: Int = 0,
    @SerialName("last_updated")
    val lastUpdated: String = ""
)

@Serializable
data class UserProgressBookView(
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: String,
    val title: String,
    val authors: List<String>,
    @SerialName("highest_image_url") val highestImageUrl: String,
    @SerialName("lowest_image_url") val lowestImageUrl: String,
    val description: String,
    @SerialName("update_history") val updateHistory: List<UpdateHistory>,
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("last_updated") val lastUpdated: String
)

@Serializable
data class UpdateHistory(
    val page: Int = 0,
    @SerialName("date_updated") val dateUpdated: String = ""
)


fun formatDate(dateString: String): String {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.ENGLISH)
    isoFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date: Date = isoFormat.parse(dateString) ?: return ""

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = date
    val year = dateCalendar.get(Calendar.YEAR)

    val format = if (year == currentYear) {
        SimpleDateFormat("MMM d", Locale.ENGLISH)
    } else {
        SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
    }

    return format.format(date)
}
