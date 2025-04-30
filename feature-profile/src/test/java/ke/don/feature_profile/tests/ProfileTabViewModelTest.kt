package ke.don.feature_profile.tests

import ke.don.feature_profile.fake.FakeProfileRepository
import ke.don.feature_profile.fake.FakeProfileUseCases
import ke.don.feature_profile.fake.FakeSettingsDataStoreManager
import ke.don.feature_profile.tab.ProfileViewModel
import ke.don.shared_domain.data_models.UserProgressBookView
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileTabViewModelTest {
    private val testDispatcher =
        StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        // Reset the Main dispatcher after the test
        Dispatchers.resetMain()
    }

    private val viewModel = ProfileViewModel(
        settingsDataStoreManager = FakeSettingsDataStoreManager(),
        profileRepository = FakeProfileRepository(),
        profileTabUseCases = FakeProfileUseCases()
    )

    val onSuccessfulSignOut = {
        println("Signout successful")
    }

    @Test
    fun `fetchUserProgress fetches user progress`() = runTest {
        // Arrange
        val state = viewModel.profileState

        // Act
        viewModel.fetchUserProgress()

        advanceUntilIdle() // This is CRITICAL

        // Assert
        assert(state.value.progressResultState is ResultState.Success)
        assertNotEquals(state.value.userProgress, emptyList<UserProgressBookView>())
    }

    @Test
    fun `updateShowSheet toggles the bottom sheet`() = runTest{
        // Arrange
        val uiState = viewModel.profileState
        val initialValue = uiState.value.showBottomSheet
        // Act
        viewModel.updateShowSheet()
        advanceUntilIdle()

        // Assert
        assertNotEquals(uiState.value.showBottomSheet, initialValue)
    }

    @Test
    fun `fetchProfile fetches profile`() = runTest{
        // Arrange
        val state = viewModel.profileState

        // Act
        viewModel.fetchProfile()

        advanceUntilIdle() // This is CRITICAL

        // Assert
        assert(state.value.profileResultState is ResultState.Success)
    }

    @Test
    fun `signOut calls onSuccessfulSignOut and is successful`() = runTest{
        // Arrange
        val state = viewModel.profileState

        // Act
        viewModel.signOut(onSuccessfulSignOut)

        advanceUntilIdle() // This is CRITICAL

        // Assert
        assert(state.value.profileResultState is ResultState.Empty)
    }

    @Test
    fun `deleteUser calls onSuccessfulSignOut and is successful`() = runTest{
        // Arrange
        val state = viewModel.profileState

        // Act
        viewModel.deleteUser(onSuccessfulSignOut)

        advanceUntilIdle() // This is CRITICAL

        // Assert
        assert(state.value.profileResultState is ResultState.Empty)
    }

}