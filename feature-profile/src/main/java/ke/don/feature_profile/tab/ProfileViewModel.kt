package ke.don.feature_profile.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.BooksRepository
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCases
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
    private val profileTabUseCases: ProfileTabUseCases,
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
        _profileState.update {
            it.copy(
                profile = state.profile,
                resultState = state.resultState,
                showBottomSheet = state.showBottomSheet
            )
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
                val result =profileTabUseCases.fetchProfileDetails(userId)
            if (result != null){
                updateProfileState(ProfileTabState(profile = result, resultState = ResultState.Success))
            }else{
                updateProfileState(ProfileTabState(resultState = ResultState.Empty))
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
            val result = profileTabUseCases.signOut()

            if (result){
                updateProfileState(
                    ProfileTabState(
                            resultState = ResultState.Empty
                        )
                    )
                    onSuccessfulSignOut()
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

            val result = profileTabUseCases.deleteUser(userId)

            if (result){
                updateProfileState(
                    ProfileTabState(
                        resultState = ResultState.Empty
                    )
                )
                onSuccessfulSignOut()
            }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}