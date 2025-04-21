package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ke.don.shared_components.components.DialogType
import ke.don.shared_components.mbuku_theme.ui.theme.LocalExtendedColorScheme


@Composable
fun PrefaceChip(
    modifier: Modifier = Modifier,
    chipType: DialogType,
    text: String
) {
    val warningColors = LocalExtendedColorScheme.current.warning

    val color = when(chipType) {
        DialogType.WARNING -> warningColors.color
        DialogType.DANGER -> MaterialTheme.colorScheme.error
        DialogType.NEUTRAL -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.primary
    }

    SuggestionChip(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = color
        ),
        colors = SuggestionChipDefaults.suggestionChipColors(
            labelColor = color
        ),
        onClick = {},
        label = { Text(text) }
    )
}