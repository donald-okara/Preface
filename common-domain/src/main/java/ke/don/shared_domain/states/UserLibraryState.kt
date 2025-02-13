package ke.don.shared_domain.states

import ke.don.shared_domain.data_models.SupabaseBookshelf

data class UserLibraryState(
    val userBookshelves: List<SupabaseBookshelf> = emptyList(),
    val successState: SuccessState = SuccessState.IDLE,
)
