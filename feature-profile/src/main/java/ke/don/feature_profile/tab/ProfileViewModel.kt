package ke.don.feature_profile.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.local.datastore.user_settings.SettingsDataStoreManager
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCases
import ke.don.shared_domain.states.ProfileTabState
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsDataStoreManager: SettingsDataStoreManager,
    private val profileRepository: ProfileRepository,
    private val profileTabUseCases: ProfileTabUseCases,
): ViewModel() {
    private val _profileState = MutableStateFlow(ProfileTabState())
    val profileState: StateFlow<ProfileTabState> = _profileState

    val settings = settingsDataStoreManager.getSettings()


    fun handleEvent(event: ProfileTabEventHandler) {
        when (event) {
            is ProfileTabEventHandler.ShowBottomSheet -> updateShowSheet()

            is ProfileTabEventHandler.FetchProfile -> fetchProfile()

            is ProfileTabEventHandler.FetchUserProgress -> fetchUserProgress()

            is ProfileTabEventHandler.SignOut -> {
                signOut(event.onSignOut)
            }
            is ProfileTabEventHandler.DeleteUser -> {
                deleteUser(event.onSignOut)
            }

            is ProfileTabEventHandler.ToggleDarkTheme -> toggleDarkTheme()
        }
    }

    fun fetchUserProgress() {
        viewModelScope.launch{
            val userId = profileRepository.fetchProfileFromDataStore().authId

            val result = profileTabUseCases.fetchProfileProgress(userId)

            _profileState.update {
                _profileState.value.copy(
                    progressResultState = ResultState.Loading
                )
            }

            if (result != null){
                _profileState.update {
                    _profileState.value.copy(
                        progressResultState = ResultState.Success,
                        userProgress = result
                    )
                }
            }else{
                _profileState.update {
                    _profileState.value.copy(
                        progressResultState = ResultState.Error()
                    )
                }
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
            _profileState.update {
                it.copy(
                    isRefreshing = true
                )
            }
            val userId = profileRepository.fetchProfileFromDataStore().authId
            val result = profileTabUseCases.fetchProfileDetails(userId)
            if (result != null){
                _profileState.update {
                    it.copy(
                        isRefreshing = false,
                        profile = result,
                        profileResultState = ResultState.Success
                    )
                }
            }else{
                _profileState.update {
                    it.copy(
                        isRefreshing = false,
                        profileResultState = ResultState.Empty
                    )
                }
            }
        }
    }

    fun signOut(onSuccessfulSignOut: () -> Unit){
        viewModelScope.launch {
            _profileState.update {
                it.copy(
                    profileResultState = ResultState.Loading
                )
            }
            val result = profileTabUseCases.signOut()

            if (result){
                _profileState.update {
                    it.copy(
                        profileResultState = ResultState.Empty
                    )
                }
                onSuccessfulSignOut()
            }else{
                _profileState.update {
                    it.copy(
                        progressResultState = ResultState.Success
                    )
                }
            }
        }
    }

    fun deleteUser(onSuccessfulSignOut: () -> Unit){
        viewModelScope.launch {
            _profileState.update {
                it.copy(
                    profileResultState = ResultState.Loading
                )
            }

            val userId = profileRepository.fetchProfileFromDataStore().authId

            val result = profileTabUseCases.deleteUser(userId)

            if (result){
              _profileState.update {
                  it.copy(
                      profileResultState = ResultState.Empty
                  )
              }
                onSuccessfulSignOut()
            }
        }
    }

    fun toggleDarkTheme(){
        viewModelScope.launch {

            val currentSettings = settings.first()
            settingsDataStoreManager.setSettings(
                currentSettings.copy(
                   darkTheme = !currentSettings.darkTheme
                )
            )
        }
    }
    companion object {
        private const val TAG = "ProfileViewModel"
    }
}