package ke.don.feature_authentication.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.feature_authentication.data.GoogleSignInClient
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.states.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
): ViewModel() {

    //val isSignInSuccessful: StateFlow<Boolean> = googleSignInClient.isSignInSuccessful
    private val _signInState: MutableStateFlow<ResultState> = MutableStateFlow(ResultState.Empty)
    val signInState: StateFlow<ResultState> = _signInState

    fun onSignInWithGoogle(onSuccessfulSignIn: () -> Unit){
        _signInState.value = ResultState.Loading
        viewModelScope.launch {
            when(val result = googleSignInClient.signInWithGoogle()){
                is NetworkResult.Success -> {
                    Log.d("SignInViewModel", "Sign in successful")
                    onSuccessfulSignIn()
                    _signInState.value = ResultState.Success
                }
                is NetworkResult.Error -> {
                    Log.d("SignInViewModel", "Sign in failed: ${result.message}")
                    _signInState.value = ResultState.Error(result.message)
                }

            }
        }

    }
}