package ke.don.feature_authentication.presentation

import android.service.autofill.OnClickAction
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GoogleSignInButton(
    onClickAction: () -> Unit
){
    Button(
        onClick = { /*TODO*/ }
    ) {
        Text(text = "Sign in with Google")
    }
}