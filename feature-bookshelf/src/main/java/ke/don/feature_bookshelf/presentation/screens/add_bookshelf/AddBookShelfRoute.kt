package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.feature_bookshelf.domain.SuccessState
import ke.don.shared_domain.data_models.BookshelfType

@Composable
fun AddBookshelfRoute(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    addBookshelfViewModel: AddBookshelfViewModel = hiltViewModel(),
){
    val state = addBookshelfViewModel.addBookshelfState.collectAsState()

    if (state.value.successState == SuccessState.SUCCESS){
        onNavigateBack()
    }

    BookshelfForm(
        modifier = modifier.padding(paddingValues),
        name = state.value.name,
        onNameChange = {
            addBookshelfViewModel.onNameChange(it)
        },
        description = state.value.description,
        onDescriptionChange = {
            addBookshelfViewModel.onDescriptionChange(it)
        },
        onAddBookshelf = {
            addBookshelfViewModel.onAddBookshelf()
        },
        bookshelfType = state.value.bookshelfType,
        onBookshelfTypeChange = {
            addBookshelfViewModel.onBookshelfTypeChange(it)
        },
        isAddButtonEnabled = addBookshelfViewModel.isAddButtonEnabled()
    )

}

@Composable
fun BookshelfForm(
    modifier: Modifier = Modifier,
    name: String,
    isAddButtonEnabled: Boolean = false,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onAddBookshelf: () -> Unit,
    bookshelfType: BookshelfType,
    onBookshelfTypeChange: (BookshelfType) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = name,
            onValueChange = {
                onNameChange(it)
            },
            label = {
                Text(text = "Name")
            }
        )
        TextField(
            value = description,
            onValueChange = {
                onDescriptionChange(it)
            },
            label ={
                Text(text = "Description")
            }
        )

        Button(
            enabled = isAddButtonEnabled,
            onClick = {onAddBookshelf()},
            modifier = modifier.fillMaxWidth()
        ){
            Text(text = "Add Bookshelf")

        }
    }
}