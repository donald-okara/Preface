package ke.don.feature_authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.feature_authentication.data.GoogleSignInClient
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
): ViewModel() {
    fun onSignInWithGoogle(){
        viewModelScope.launch {
            googleSignInClient.signInWithGoogle()

        }
    }
}