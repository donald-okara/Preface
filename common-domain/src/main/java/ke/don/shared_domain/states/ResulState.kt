package ke.don.shared_domain.states

sealed interface ResultState {
    data object Success : ResultState
    data class Error(val message: String = "An error occurred") : ResultState
    data object Loading : ResultState
    data object Empty : ResultState
}