package ke.don.feature_book_details.domain.logger

interface Logger {
    fun logDebug(tag: String, message: String)

    fun logError(tag: String, message: String)
}
