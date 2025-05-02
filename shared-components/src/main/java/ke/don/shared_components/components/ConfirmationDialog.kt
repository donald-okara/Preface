package ke.don.shared_components.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ke.don.shared_components.R
import ke.don.shared_components.mbuku_theme.ui.theme.LocalExtendedColorScheme
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    dialogType: DialogType = DialogType.NEUTRAL,
    icon: ImageVector,
) {
    val warningColors = LocalExtendedColorScheme.current.warning

    val containerColor = when(dialogType) {
        DialogType.WARNING -> warningColors.colorContainer
        DialogType.DANGER -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val onContainerColor = when(dialogType) {
        DialogType.WARNING -> warningColors.color
        DialogType.DANGER -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        //containerColor = containerColor,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = dialogText,
                tint = onContainerColor
            )
        },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(
                    text = stringResource(R.string.confirm),
                    color = onContainerColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = onContainerColor
                )
            }
        },
        modifier = modifier
    )
}


enum class DialogType {
    NEUTRAL,
    INFO,
    WARNING,
    DANGER
}

@Preview
@Composable
fun ConfirmationNeutralDialogPreview(){
    MbukuTheme {
        ConfirmationDialog(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Neutral",
            dialogText = "Are you sure you want to delete this bookshelf?",
            icon = Icons.Outlined.Close,
            dialogType = DialogType.NEUTRAL
        )
    }
}
@Preview
@Composable
fun ConfirmationInfoDialogPreview(){
    MbukuTheme {
        ConfirmationDialog(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Info",
            dialogText = "Are you sure you want to delete this bookshelf?",
            icon = Icons.Outlined.Close,
            dialogType = DialogType.INFO
        )
    }
}
@Preview
@Composable
fun ConfirmationWarningDialogPreview(){
    MbukuTheme {
        ConfirmationDialog(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Warning",
            dialogText = "Are you sure you want to delete this bookshelf?",
            icon = Icons.Outlined.Close,
            dialogType = DialogType.WARNING
        )
    }
}
@Preview
@Composable
fun ConfirmationDangerDialogPreview(){
    MbukuTheme {
        ConfirmationDialog(
            onDismissRequest = {},
            onConfirmation = {},
            dialogTitle = "Danger",
            dialogText = "Are you sure you want to delete this bookshelf?",
            icon = Icons.Outlined.Close,
            dialogType = DialogType.DANGER
        )
    }
}