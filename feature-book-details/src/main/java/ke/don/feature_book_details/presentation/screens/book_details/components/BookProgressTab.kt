package ke.don.feature_book_details.presentation.screens.book_details.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dagger.hilt.android.qualifiers.ApplicationContext
import ke.don.common_datasource.remote.domain.states.BookUiState
import ke.don.feature_book_details.R
import ke.don.feature_book_details.presentation.screens.book_details.BookDetailsEvent
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_components.components.IndividualReadingProgressCard
import ke.don.shared_domain.states.ResultState
import kotlin.math.roundToInt

@Composable
fun BookProgressTab(
    modifier: Modifier = Modifier,
    progressColor: Color,
    onBookDetailsEvent: (BookDetailsEvent) -> Unit,
    bookUiState: BookUiState,
){
    when(bookUiState.userProgressState.resultState){
        is ResultState.Loading -> {
            EmptyScreen(
                modifier = modifier.fillMaxSize(),
                icon = Icons.Outlined.HourglassEmpty,
                textColor = progressColor,
                message = stringResource(R.string.loading),
                action = {},
                actionText = bookUiState.loadingJoke
            )
        }
        is ResultState.Success -> {
            Column(
                modifier = modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IndividualReadingProgressCard(
                    modifier = modifier,
                    color = progressColor,
                    currentPage = bookUiState.userProgressState.bookProgress.currentPage,
                    totalPages = bookUiState.userProgressState.bookProgress.totalPages
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
                        onClick = {onBookDetailsEvent(BookDetailsEvent.ToggleUpdateProgressDialog(toggle = true))},
                        modifier = modifier.fillMaxWidth()
                    ){
                        Text(
                            text = stringResource(R.string.update_progress)
                        )
                    }
                }
            }

            if (bookUiState.showUpdateProgressDialog.showOption){
                AddProgressDialog(
                    modifier = modifier,
                    onDismissRequest = {onBookDetailsEvent(BookDetailsEvent.ToggleUpdateProgressDialog(toggle = true))},
                    onConfirmation = {onBookDetailsEvent(BookDetailsEvent.SaveBookProgress)},
                    bookProgress = bookUiState.userProgressState.newProgress,
                    onBookProgressUpdate = { onBookDetailsEvent(BookDetailsEvent.ChangeCurrentPage(it)) },
                    dialogTitle = stringResource(R.string.add_progress),
                    icon = Icons.Outlined.AutoStories,
                    dialogText = stringResource(R.string.book_progress),
                    enabled = !bookUiState.userProgressState.isError,
                    maxProgress = bookUiState.userProgressState.bookProgress.totalPages
                )
            }
        }

        else -> {
            EmptyScreen(
                modifier = modifier.fillMaxSize(),
                icon = Icons.Outlined.Error,
                textColor = progressColor,
                message = stringResource(R.string.error),
                action = {onBookDetailsEvent(BookDetailsEvent.FetchProgress)},
                actionText = stringResource(R.string.something_went_wrong_please_try_again)
            )
        }
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
    icon: ImageVector,
    minProgress: Int = 1,
    maxProgress: Int
) {
    val context = LocalContext.current.applicationContext
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var sliderPosition by remember { mutableFloatStateOf(bookProgress.toFloat() / maxProgress.toFloat()) }
    var currentProgress by remember { mutableIntStateOf(bookProgress) }

    fun updateProgress(value: Int) {
        currentProgress = value
        onBookProgressUpdate(value)
        sliderPosition = value.toFloat() / maxProgress.toFloat()
    }

    fun isValidInput(input: String): Boolean {
        return input.all { it.isDigit() } &&
                input.toIntOrNull() in minProgress..maxProgress
    }

    AlertDialog(
        icon = { Icon(imageVector = icon, contentDescription = dialogTitle) },
        title = { Text(text = dialogTitle) },
        text = {
            ProgressInputContent(
                dialogText = dialogText,
                currentProgress = currentProgress,
                sliderPosition = sliderPosition,
                maxProgress = maxProgress,
                minProgress = minProgress,
                errorMessage = errorMessage,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        currentProgress = 0
                        sliderPosition = 0f
                        onBookProgressUpdate(0)
                        errorMessage = null
                    } else if (isValidInput(newValue)) {
                        val value = newValue.toInt()
                        updateProgress(value)
                        errorMessage = null
                    } else {
                        errorMessage =
                            context.getString(R.string.progress_must_be, minProgress.toString(), maxProgress.toString())
                    }
                },
                onSliderChange = {
                    sliderPosition = it
                    val page = (it * maxProgress).roundToInt().coerceIn(minProgress, maxProgress)
                    updateProgress(page)
                }
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation, enabled = enabled) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.dismiss))
            }
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressInputContent(
    dialogText: String,
    currentProgress: Int,
    sliderPosition: Float,
    maxProgress: Int,
    minProgress: Int,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onSliderChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            label = { Text(text = dialogText) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = currentProgress.takeIf { it != 0 }?.toString() ?: "",
            onValueChange = onValueChange
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Page: $currentProgress / $maxProgress")

        Slider(
            value = sliderPosition,
            onValueChange = onSliderChange,
            valueRange = 0f..1f,
            steps = 10,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    thumbSize = DpSize(24.dp, 24.dp)
                )
            }
        )
    }
}

