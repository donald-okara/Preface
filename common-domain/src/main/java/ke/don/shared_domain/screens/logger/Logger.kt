package ke.don.shared_domain.screens.logger

interface Logger {
    fun logDebug(tag: String, message: String)

    fun logError(tag: String, message: String)
}
