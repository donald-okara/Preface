package ke.don.shared_domain.states

sealed interface ResultState {
    data object Success : ResultState
    data class Error(val message: String = "An error occurred") : ResultState
    data object Loading : ResultState
    data object Empty : ResultState
}

sealed interface NetworkResult<out T : Any?> {
    data class Success<T: Any?> (val data: T): NetworkResult<T>
    data class Error(
        val code: String? = null,
        val details: String? = null,
        val hint: String? = null,
        val message: String,
        val category: ErrorCategory? = ErrorCategory.UNKNOWN
    ) : NetworkResult<Nothing>
}

// Enum to categorize errors
enum class ErrorCategory {
    API, // Errors from Supabase API (e.g., validation, conflicts)
    NETWORK, // General network issues (e.g., no connectivity)
    TIMEOUT, // Request timeouts
    UNKNOWN // Unhandled or unexpected errors
}