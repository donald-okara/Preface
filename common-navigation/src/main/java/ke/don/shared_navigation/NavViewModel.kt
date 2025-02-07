package ke.don.shared_navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {
    private val _isSignedIn = MutableStateFlow(false)
    val isSignedIn: StateFlow<Boolean> = _isSignedIn

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkUserSignInStatus()
    }

    private fun checkUserSignInStatus(){
        viewModelScope.launch {
            try {
                _isLoading.update { true }

                _isSignedIn.value = profileRepository.checkSignedInStatus()

                Log.d("NavViewModel", "Is user signed in? ${_isSignedIn.value}")
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                _isLoading.update { false }
            }
        }
    }

}

