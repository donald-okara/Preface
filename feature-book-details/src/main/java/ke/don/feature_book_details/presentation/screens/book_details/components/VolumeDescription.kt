package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.don.shared_domain.data_models.VolumeInfoDet
import ke.don.shared_domain.utils.formatting_utils.formatHtmlToAnnotatedString

@Composable
fun AboutVolume(
    modifier: Modifier = Modifier,
    volumeInfo: VolumeInfoDet,
    textColor: Color = MaterialTheme.colorScheme.primary
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

        Spacer(modifier = modifier.height(8.dp))

        // Show More/Less toggle
        if (!isExpanded && styledText.length > maxLinesCollapsed * 50) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.clickable { isExpanded = true }
            ) {
                Text(
                    text = "Show more",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    tint = textColor,
                    contentDescription = "Show more"
                )
            }
        } else if (isExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.clickable { isExpanded = false }
            ) {
                Text(
                    text = "Show less",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    tint = textColor,
                    contentDescription = "Show less"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDescriptionColumn() {
    DescriptionColumn(description = "<b>This is bold text</b>, <i>this is italic text</i>, and <u>this is underlined</u>.")
}

