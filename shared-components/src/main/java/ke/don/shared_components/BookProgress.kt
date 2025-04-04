package ke.don.shared_components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme


@Composable
fun IndividualReadingProgressCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    currentPage: Int,
    totalPages: Int
) {
    val derivedValues by remember {
        derivedStateOf {
            val progress = currentPage.toFloat() / totalPages.toFloat()
            val pagesLeft = totalPages - currentPage
            Triple(progress, currentPage, pagesLeft)
        }
    }

    val (progress, completedPages, pagesLeft) = derivedValues
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                text = "Reading Progress",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = modifier.height(16.dp))


            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$completedPages of $totalPages pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }

            Spacer(modifier = modifier.height(8.dp))

            RoundedProgressBar(
                progress = progress,
                progressColor = color,
                modifier = modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = modifier.height(16.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = color
                    )

                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = totalPages.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Pages left",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }

        }
    }
}

@Composable
fun RoundedProgressBar(
    modifier: Modifier = Modifier,
    progress: Float, // Range: 0f to 1f
     // Explicit height to make it visible
    backgroundColor: Color = Color.Gray,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 12.dp
) {
    Canvas(modifier = modifier
        .fillMaxWidth() // Ensures it takes full width
        .height(12.dp)
    ) {
        val barWidth = size.width
        val barHeight = height.toPx()
        val radius = barHeight / 2
        val progressWidth = barWidth * progress

        // Draw background bar
        drawRoundRect(
            color = backgroundColor,
            size = Size(barWidth, barHeight),
            cornerRadius = CornerRadius(radius, radius),
            topLeft = Offset(0f, (size.height - barHeight) / 2)
        )

        // Draw progress with rounded ends
        if (progress > 0) {
            drawRoundRect(
                color = progressColor,
                size = Size(progressWidth, barHeight),
                cornerRadius = CornerRadius(radius, radius),
                topLeft = Offset(0f, (size.height - barHeight) / 2)
            )
        }
    }
}


@Preview
@Composable
fun ProgressBarPreviewDarkTheme(){
    MbukuTheme(darkTheme = true){
        RoundedProgressBar(
            progress = 0.5f
        )
    }
}

@Preview
@Composable
fun ProgressBarPreviewLightTheme(){
    MbukuTheme(darkTheme = false){
        RoundedProgressBar(
            progress = 0.5f
        )
    }
}

@Preview
@Composable
fun IndividualReadingProgressCardPreview(){
    MbukuTheme(darkTheme = true) {
        IndividualReadingProgressCard(
            currentPage = 120,
            totalPages = 240
        )
    }
}

@Preview
@Composable
fun IndividualReadingProgressCardPreviewLight(){
    MbukuTheme(darkTheme = false) {
        IndividualReadingProgressCard(
            currentPage = 120,
            totalPages = 240
        )
    }
}
