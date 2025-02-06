package ke.don.feature_authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.feature_authentication.data.GoogleSignInClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
): ViewModel() {

    val isSignInSuccessful: StateFlow<Boolean> = googleSignInClient.isSignInSuccessful


    fun onSignInWithGoogle(){
        try {
            viewModelScope.launch {
                googleSignInClient.signInWithGoogle()

            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}