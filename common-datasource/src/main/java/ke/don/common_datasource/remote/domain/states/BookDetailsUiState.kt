package ke.don.common_datasource.remote.domain.states

import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.ResultState
import ke.don.shared_domain.states.loadingBookJokes
import ke.don.shared_domain.utils.color_utils.model.ColorPallet
import kotlin.random.Random

data class BookUiState(
    val volumeId : String? = null,
    val userId: String? = null,
    val bookDetails: BookDetailsResponse = BookDetailsResponse(),
    val userProgressState: UserProgressState = UserProgressState(),
    val bookshelvesState: BookshelvesState = BookshelvesState(),
    val showUpdateProgressDialog: ShowOptionState = ShowOptionState(),
    val showBookshelvesDropDown: ShowOptionState = ShowOptionState(),
    val showBottomSheet: ShowOptionState = ShowOptionState(),
    val colorPallet: ColorPallet = ColorPallet(),
    val highestImageUrl: String? = null,
    val loadingJoke: String = loadingBookJokes[Random.nextInt(loadingBookJokes.size)],
    val resultState: ResultState = ResultState.Loading,
)


data class UserProgressState(
    val bookProgress: UserProgressResponse = UserProgressResponse(),
    val resultState: ResultState = ResultState.Loading,
    val isPresent : Boolean = false,
    val isError: Boolean = true,
    val newProgress: Int = 0,
)

data class BookshelfBookDetailsState(
    val bookshelfBookDetails: BookshelfEntity = BookshelfEntity(),
    val isSelected: Boolean = false,
    val isBookPresent: Boolean = false,
)

data class BookshelvesState(
    val bookshelves: List<BookshelfBookDetailsState> = emptyList(),
    val resultState: ResultState = ResultState.Loading
)

data class ShowOptionState(
    val showOption : Boolean = false,
    val isLoading : Boolean = false,
)


