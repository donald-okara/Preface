package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.don.feature_book_details.data.model.VolumeInfoDet
import ke.don.shared_domain.utils.formatting_utils.formatHtmlToAnnotatedString

@Composable
fun AboutVolume(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(8.dp)
    ) {
        DescriptionColumn(
            description = volumeInfo.description,
            textColor = textColor,
        )
    }
}

@Composable
fun DescriptionColumn(
    modifier: Modifier = Modifier,
    description: String,
    textColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    maxLinesCollapsed: Int = 3 // You can set the number of lines to show when collapsed
) {
    // State to track if the description is expanded or collapsed
    var isExpanded by remember { mutableStateOf(false) }

    // Format the description
    val styledText =
        formatHtmlToAnnotatedString(
            description
        )

    // Text with conditional maxLines
    Column {
        // Display the styled text
        Text(
            text = styledText,
            maxLines = if (isExpanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis // Show ellipsis if text is truncated
        )

        // Show More/Less toggle
        if (!isExpanded && styledText.length > maxLinesCollapsed * 50) {
            Text(
                text = "Show More",
                color = textColor,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = modifier
                    .clickable { isExpanded = true }
                    .padding(top = 4.dp)
            )
        } else if (isExpanded) {
            Text(
                text = "Show Less",
                color = textColor,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = modifier
                    .clickable { isExpanded = false }
                    .padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDescriptionColumn() {
    DescriptionColumn(description = "<b>This is bold text</b>, <i>this is italic text</i>, and <u>this is underlined</u>.")
}

