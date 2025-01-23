package ke.don.feature_book_details.domain.states

import ke.don.feature_book_details.data.model.BookDetailsResponse
import ke.don.shared_domain.utils.color_utils.model.ColorPallet

data class BookUiState(
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val resultState: ResultState = ResultState.Empty

)

sealed interface ResultState {
    data object Success : ResultState
    data class Error(val message: String = "An error occurred") : ResultState
    data object Loading : ResultState
    data object Empty : ResultState
}
