package ke.don.shared_domain.states

sealed interface ResultState {
    data object Success : ResultState
    data class Error(val message: String = "An error occurred") : ResultState
    data object Loading : ResultState
    data object Empty : ResultState
}

sealed interface NetworkResult<out T : Any> {
    data class Success<T: Any> (val result: T): NetworkResult<T>
    data class Error(
        val code: String? = null,
        val details: String? = null,
        val hint: String? = null,
        val message: String
    ) : NetworkResult<Nothing>
}