package ke.don.feature_profile.fake

import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.data_models.UserProgressBookView

object FakeProfileDetails {
    val fakeProfileDetails = ProfileDetails(
        id = 123,
        name = "User Name",
        authId = "gjsdijlkd",
        avatarUrl = "https/hdjjdmccm",
        discoveredBooks = 12,
        email = "william.henry.harrison@example-pet-store.com"
    )

    val fakeProfile = Profile(
        id = 123,
        name = "User Name",
        authId = "gjsdijlkd",
        avatarUrl = "https/hdjjdmccm",
        email = "william.henry.harrison@example-pet-store.com"
    )

    val fakeUserProgress = UserProgressBookView(
        userId = "gjsdijlkd",
        bookId = "book_456",
        title = "The Secrets of Kotlin",
        authors = listOf("Jane Doe", "John Smith"),
        highestImageUrl = "https://example.com/images/book_high.jpg",
        lowestImageUrl = "https://example.com/images/book_low.jpg",
        description = "A deep dive into Kotlin programming with practical examples and best practices.",
        currentPage = 87,
        totalPages = 300,
        lastUpdated = "2025-04-17T10:15:30Z",
        updateHistory = emptyList()
    )
}
