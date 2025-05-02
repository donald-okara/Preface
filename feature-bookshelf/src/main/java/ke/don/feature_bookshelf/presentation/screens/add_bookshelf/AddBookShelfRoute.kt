package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ke.don.feature_bookshelf.R
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfViewModel.Companion.MAX_DESCRIPTION_LENGTH
import ke.don.feature_bookshelf.presentation.screens.add_bookshelf.AddBookshelfViewModel.Companion.MAX_NAME_LENGTH
import ke.don.shared_domain.states.AddBookshelfState
import ke.don.shared_domain.states.SuccessState

@Composable
fun AddBookshelfRoute(
    modifier: Modifier = Modifier,
    state: AddBookshelfState,
    handleEvent: (AddBookshelfEventHandler) -> Unit,
    onNavigateBack: () -> Unit,
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ){
        BookshelfForm(
            modifier = modifier,
            handleEvent = handleEvent,
            state = state,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun BookshelfForm(
    modifier: Modifier = Modifier,
    handleEvent: (AddBookshelfEventHandler) -> Unit,
    state: AddBookshelfState,
    onNavigateBack: () -> Unit,
){
    val isAddButtonEnabled = state.name.isNotEmpty() && state.successState != SuccessState.LOADING

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        FormTextField(
            value = state.name,
            onValueChange = {
                handleEvent(AddBookshelfEventHandler.OnNameChange(it))
            },
            maxCharacters = MAX_NAME_LENGTH,
            label = (stringResource(R.string.name)),

        )
        FormTextField(
            value = state.description,
            onValueChange = {
                handleEvent(AddBookshelfEventHandler.OnDescriptionChange(it))
            },
            maxCharacters = MAX_DESCRIPTION_LENGTH,
            label = stringResource(R.string.description),

        )

        Spacer(modifier = modifier.padding(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.padding(8.dp)
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f), // Takes half the available width
                onClick = onNavigateBack
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f), // Takes half the available width
                onClick = {
                    handleEvent(AddBookshelfEventHandler.OnSubmit(onNavigateBack))
                },
                enabled = isAddButtonEnabled,
            ) {
                Text(text = stringResource(R.string.done))
            }
        }
    }
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    maxCharacters: Int,
    onValueChange: (String) -> Unit,
    label: String
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = {
                Text(text = label)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .fillMaxWidth()
        )

        Text(
            text = "${value.length}/$maxCharacters",
            textAlign = TextAlign.End,
            color = if(value.length >= maxCharacters - 10) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            modifier = modifier.align(Alignment.End)
        )
    }
}

@Preview
@Composable
fun BookshelfFormPreview(){
    BookshelfForm(
        handleEvent = {},
        state = AddBookshelfState(),
        onNavigateBack = {}
    )
}