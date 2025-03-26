package ke.don.feature_profile.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ProfileTabState
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val bookRepository: BooksRepository
): ViewModel() {

    private val _profileState = MutableStateFlow(ProfileTabState())
    val profileState = _profileState
        .onStart { fetchProfile() }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileTabState()
        )

    fun updateProfileState(state: ProfileTabState) {
        _profileState.update { currentState ->
            if (currentState.profile == state.profile && currentState.resultState == state.resultState) {
                currentState // Prevent unnecessary updates
            } else {
                currentState.copy(
                    profile = state.profile ?: currentState.profile, // Preserve profile if not provided
                    resultState = state.resultState
                )
            }
        }
    }

    fun updateShowSheet(){
        _profileState.update { currentState ->
            currentState.copy(
                showBottomSheet = !currentState.showBottomSheet
            )
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            val userId = profileRepository.fetchProfileFromDataStore().authId

            updateProfileState(ProfileTabState(resultState = ResultState.Loading))
            when (val profile = profileRepository.fetchProfileDetails(userId)){
                is NetworkResult.Error -> {
                    updateProfileState(ProfileTabState(resultState = ResultState.Error()))
                }

                is NetworkResult.Success -> {
                    updateProfileState(ProfileTabState(profile = profile.data, resultState = ResultState.Success))

                }
            }

        }
    }

    fun signOut(onSuccessfulSignOut: () -> Unit){
        viewModelScope.launch {
            updateProfileState(
                ProfileTabState(
                    resultState = ResultState.Loading
                )
            )
            when(val result = profileRepository.signOut()){
                is NetworkResult.Error -> {
                    updateProfileState(
                        ProfileTabState(
                            resultState = ResultState.Success
                        )
                    )
                }
                is NetworkResult.Success -> {
                    updateProfileState(
                        ProfileTabState(
                            resultState = ResultState.Empty
                        )
                    )
                    onSuccessfulSignOut()
                }
            }
        }
    }

    fun deleteUser(onSuccessfulSignOut: () -> Unit){
        viewModelScope.launch {
            updateProfileState(
                ProfileTabState(
                    resultState = ResultState.Loading
                )
            )
            val userId = profileRepository.fetchProfileFromDataStore().authId

            when(val result = profileRepository.deleteUser(userId)){
                is NetworkResult.Error -> {
                    updateProfileState(
                        ProfileTabState(
                            resultState = ResultState.Success
                        )
                    )
                }
                is NetworkResult.Success -> {
                    updateProfileState(
                        ProfileTabState(
                            resultState = ResultState.Empty
                        )
                    )
                    onSuccessfulSignOut()
                }
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}