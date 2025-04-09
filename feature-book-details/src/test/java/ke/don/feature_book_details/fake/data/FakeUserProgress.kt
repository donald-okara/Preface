package ke.don.feature_book_details.fake.data

import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.ResultState

object FakeUserProgress {
    // Sample fake data for UserProgressResponse
    val fakeUserProgressResponse = UserProgressResponse(
        userId = "user123",
        bookId = "book456",
        currentPage = 42,
        totalPages = 120,
        lastUpdated = "2023-10-08T15:30:00Z"
    )

    // Sample fake data for UserProgressState
    val fakeUserProgressState = UserProgressState(
        bookProgress = fakeUserProgressResponse,
        resultState = ResultState.Success, // Replace with actual success state or another relevant state.
        isPresent = true,
        isError = false,
        newProgress = 42
    )

}