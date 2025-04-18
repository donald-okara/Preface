package ke.don.shared_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import coil.compose.AsyncImage
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme


@Composable
fun CurrentlyReadingCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    currentPage: Int,
    lastUpdated: String,
    title: String,
    totalPages: Int,
    previewPainter: Painter? = null
) {
    val progress by remember {
        derivedStateOf {
            currentPage.toFloat() / totalPages.toFloat()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(16.dp)) {
        if (LocalInspectionMode.current && previewPainter != null) {
            Image(
                painter = previewPainter,
                contentDescription = "$title book cover",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .width(100.dp)
                    .aspectRatio(3f / 4f)
                    .scale(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "$title book cover",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .width(100.dp)
                    .aspectRatio(3f / 4f)
                    .scale(1f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = modifier.width(16.dp))

        Column {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Last updated $lastUpdated",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))



            Text(
                text = "${(progress * 100).toInt()}% completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            RoundedProgressBar(
                progress = progress,
                modifier = modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }

}


@Composable
fun FinishedBookCard(
    modifier: Modifier = Modifier,
    title: String,
    dateCompleted: String,
    previewPainter: Painter? = null,
    imageUrl: String
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (LocalInspectionMode.current && previewPainter != null) {
            Image(
                painter = painterResource(R.drawable.download),
                contentDescription = "$title book cover",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .width(100.dp)
                    .aspectRatio(3f / 4f)
                    .scale(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "$title book cover",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .width(100.dp)
                    .aspectRatio(3f / 4f)
                    .scale(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = modifier.width(16.dp))

        Column {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Completed on $dateCompleted",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = modifier.height(8.dp))

        }
    }

}

@Preview(showBackground = true)
@Composable
fun CurrentlyReadingCardPreview(){
    MbukuTheme(darkTheme = true) {
       CurrentlyReadingCard(
           currentPage = 50,
           totalPages = 100,
           imageUrl = "",
           lastUpdated = "",
           previewPainter = painterResource(R.drawable.download),
           title = ""
       )
    }
}


@Preview(showBackground = true)
@Composable
fun CurrentlyReadingCardLightPreview(){
    MbukuTheme(darkTheme = false) {
       CurrentlyReadingCard(
           imageUrl = "",
           previewPainter = painterResource(R.drawable.download),
           currentPage = 50,
           lastUpdated = "",
           totalPages = 100,
           title = ""
       )
    }
}

@Preview(showBackground = true)
@Composable
fun FinishedBookCardPreview(){
    MbukuTheme(
        darkTheme = false
    ) {
       FinishedBookCard(
           title = "Harry Potter",
           dateCompleted = "Mar 15th",
           imageUrl = "",
           previewPainter = painterResource(R.drawable.download),
       )
    }
}
@Preview(showBackground = true)
@Composable
fun FinishedBookCardPreviewLight(){
    MbukuTheme(darkTheme = true) {
       FinishedBookCard(
           title = "Harry Potter",
           dateCompleted = "Mar 15th",
           imageUrl = "",
           previewPainter = painterResource(R.drawable.download),
       )
    }
}
