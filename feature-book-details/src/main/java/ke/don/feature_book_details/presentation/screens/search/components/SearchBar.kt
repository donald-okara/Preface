package ke.don.feature_book_details.presentation.screens.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.don.feature_book_details.R

@Composable
fun BookSearchBar(
    modifier: Modifier = Modifier,
    bookSearch: String,
    onBookSearchChange: (String) -> Unit,
    suggestedBook: String,
    isSearchPopulated: Boolean = false,
    onClear: () -> Unit = {},
    onShuffle: () -> Unit,
    shape: RoundedCornerShape,
    onSearch: () -> Unit,
    maxCharacters: Int = 50 // Add a maxCharacters parameter

){
    val focusRequester = remember { FocusRequester() } // For controlling focus
    val focusManager = LocalFocusManager.current // To clear focus and dismiss keyboard

    OutlinedTextField(
        value = bookSearch,
        singleLine = true,
        placeholder = {
            Text(
                stringResource(R.string.search_hint, suggestedBook),
                maxLines = 1, // Placeholder will be on a single line
                textAlign = TextAlign.Start
            )
        },
        onValueChange = { newValue ->
            if (newValue.length <= maxCharacters) {
                onBookSearchChange(newValue) // Only update the state if the limit isn't exceeded
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search // Set the IME action to Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch() // Trigger search when the search action is performed
                focusManager.clearFocus() // Clear focus on search
            }
        ),
        trailingIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = { onShuffle() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = stringResource(R.string.shuffle)
                    )
                }
                IconButton(
                    onClick = {
                        onSearch()
                        focusManager.clearFocus() // Clear focus when search is pressed
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }
        },

        shape = shape,
        leadingIcon = {
            IconButton(
                onClick = {
                    onClear()
                },
                enabled = isSearchPopulated
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester) // Attach the focusRequester to the text field

    )
}
