package ke.don.feature_authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.feature_authentication.data.GoogleSignInClient
import ke.don.shared_domain.states.EmptyResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
): ViewModel() {

    //val isSignInSuccessful: StateFlow<Boolean> = googleSignInClient.isSignInSuccessful
    private val _signInState: MutableStateFlow<EmptyResultState> = MutableStateFlow(EmptyResultState.Empty)
    val signInState: StateFlow<EmptyResultState> = _signInState

    fun onSignInWithGoogle(onSuccessfulSignIn: () -> Unit){
        try {
            _signInState.value = EmptyResultState.Loading
            viewModelScope.launch {
                if(googleSignInClient.signInWithGoogle() == EmptyResultState.Success){
                    onSuccessfulSignIn()
                    _signInState.value = EmptyResultState.Success
                }else{
                    _signInState.value = EmptyResultState.Error("Sign in failed")
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            _signInState.value = EmptyResultState.Error(e.message ?: "Unknown error")
        }

    }
}