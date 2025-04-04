package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ke.don.common_datasource.remote.domain.states.UserProgressState
import ke.don.shared_components.IndividualReadingProgressCard

@Composable
fun BookProgressTab(
    modifier: Modifier = Modifier,
    progressColor: Color,
    userProgressState: UserProgressState,
    onBookProgressUpdate: (Int) -> Unit,
    onSaveProgress : () -> Unit,
    onShowOptionsDialog: () -> Unit,
){
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IndividualReadingProgressCard(
            modifier = modifier,
            color = progressColor,
            currentPage = userProgressState.bookProgress.currentPage,
            totalPages = userProgressState.bookProgress.totalPages
        )

        Spacer(modifier = modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxWidth()
        ){
            Button(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = progressColor
                ),
                onClick = {onShowOptionsDialog()},
                modifier = modifier.fillMaxWidth()
            ){
                Text(
                   text = "Update Progress"
                )
            }
        }
    }

    if (userProgressState.showUpdateProgressDialog.showOption){
        AddProgressDialog(
            modifier = modifier,
            onDismissRequest = {onShowOptionsDialog()},
            onConfirmation = {onSaveProgress()},
            bookProgress = userProgressState.newProgress,
            onBookProgressUpdate = onBookProgressUpdate,
            dialogTitle = "Add Progress",
            icon = Icons.Outlined.AutoStories,
            dialogText = "Book progress",
            enabled = !userProgressState.isError,
            maxProgress = userProgressState.bookProgress.totalPages
        )
    }

}

@Composable
fun AddProgressDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    bookProgress: Int,
    enabled: Boolean,
    onBookProgressUpdate: (Int) -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon : ImageVector,
    minProgress: Int = 1,
    maxProgress: Int
){
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun isValidInput(input: String, currentProgress: Int): Boolean {
        return input.all { it.isDigit() } &&
                input.toInt() in minProgress..maxProgress
    }

    AlertDialog(
        //containerColor = containerColor,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = dialogTitle
            )
        },
        title = { Text(text = dialogTitle) },
        text = {
            Column {
                TextField(
                    label = { Text(text = dialogText) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = if (bookProgress == 0) {
                        "" // If progress is 0, display an empty string
                    } else bookProgress.toString(),
                    onValueChange = { newValue ->
                        // Only validate if the value is not empty
                        if (newValue.isEmpty() || isValidInput(newValue, bookProgress)) {
                            val newProgress = newValue.toIntOrNull() ?: 0 // Safely handle empty string by defaulting to 0
                            errorMessage = null // Clear the error message if input is valid
                            onBookProgressUpdate(newProgress)
                        } else {
                            errorMessage = "Progress must be between $minProgress and $maxProgress"
                        }
                    },
                )

                // Display error message if value is invalid
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation,
                enabled = enabled
            ) {
                Text(
                    text = "Confirm"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Dismiss"
                )
            }
        },
        modifier = modifier
    )
}