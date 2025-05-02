package ke.don.shared_components.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SheetOptionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onOptionClick: () -> Unit,
    trailingItem: @Composable (() -> Unit)? = null
) {
    val textSize = 18f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOptionClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp)
        )
        Spacer(modifier = Modifier.weight(1f))
        trailingItem?.invoke()
    }
}

@Composable
fun PrefaceSnackBar(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    contentColor: Color,
    onOptionClick: () -> Unit,
    trailingItem: @Composable (() -> Unit)? = null
){
    val textSize = 18f
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        tonalElevation = 6.dp,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                tint = contentColor,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = contentColor,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp)
            )
            Spacer(modifier = Modifier.weight(1f))
            trailingItem?.invoke()
        }
    }
}

