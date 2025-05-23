package ke.don.feature_authentication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.don.feature_authentication.R

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit,
    enabled: Boolean = true,
) {
    val containerColor = MaterialTheme.colorScheme.inverseSurface
    val contentColor = MaterialTheme.colorScheme.inverseOnSurface

    Button(
        onClick = onClickAction,
        shape = RoundedCornerShape(64.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth() // Ensures proper spacing across the button
        ) {
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = stringResource(R.string.google_logo),
                modifier = Modifier
                    .size(32.dp) // Scaled to fit within the button without truncation
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.continue_with_google),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis // Prevents overflow issues
            )
        }
    }
}


@Preview
@Composable
fun GoogleSignInButtonPreview(){
    GoogleSignInButton(
        onClickAction = {}
    )
}