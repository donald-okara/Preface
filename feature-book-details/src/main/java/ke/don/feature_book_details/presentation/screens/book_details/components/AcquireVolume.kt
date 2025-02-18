package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

//Tab to be implemented once rights are secured
@Composable
fun AcquireBookItem(
    modifier: Modifier = Modifier,
    image : Int,
    contentDescription : String,
    onClick : () -> Unit = {},
    text : String,
){
    ListItem(
        headlineContent = {
            Text(
                text = contentDescription,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(8.dp)
            )
        },
        supportingContent = {
            Text(
                text = text,
                textAlign = TextAlign.Start,
                maxLines = 2,
            )
        },
        leadingContent = {
            Image(
                painter = painterResource(id = image),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(32.dp))
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}