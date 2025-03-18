package ke.don.shared_domain.states

sealed interface EmptyResultState {
    data object Success : EmptyResultState
    data class Error(val message: String = "An error occurred") : EmptyResultState
    data object Loading : EmptyResultState
    data object Empty : EmptyResultState
}

