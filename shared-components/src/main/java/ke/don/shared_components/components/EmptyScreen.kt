package ke.don.shared_components.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    message: String,
    textColor: Color = MaterialTheme.colorScheme.primary,
    action: () -> Unit,
    actionText: String
){
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(
            imageVector = icon,
            modifier = modifier.size(64.dp),
            contentDescription = "Empty icon"
        )

        Spacer(modifier = modifier.padding(16.dp))

        Text(
            text = message,
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        )

        TextButton(
            onClick = action
        ) {
            Text(
                text = actionText,
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
            )
        }
    }
}