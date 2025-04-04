package ke.don.shared_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SheetOptionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onOptionClick: () -> Unit
) {
    val textSize = 18f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {onOptionClick()}
            .padding(8.dp), // Inner padding for content
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = modifier.size(24.dp)
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp)
        )
    }

}

