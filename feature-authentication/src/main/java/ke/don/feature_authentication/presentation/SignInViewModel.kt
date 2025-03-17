package ke.don.feature_authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.feature_authentication.data.GoogleSignInClient
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
        try {
            _signInState.value = ResultState.Loading
            viewModelScope.launch {
                if(googleSignInClient.signInWithGoogle() == ResultState.Success){
                    onSuccessfulSignIn()
                    _signInState.value = ResultState.Success
                }else{
                    _signInState.value = ResultState.Error("Sign in failed")
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            _signInState.value = ResultState.Error(e.message ?: "Unknown error")
        }

    }
}