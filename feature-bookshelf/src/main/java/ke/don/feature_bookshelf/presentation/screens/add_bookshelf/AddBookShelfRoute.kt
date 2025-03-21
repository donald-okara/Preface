package ke.don.feature_bookshelf.presentation.screens.add_bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.shared_domain.data_models.BookshelfType

@Composable
fun AddBookshelfRoute(
    bookshelfId : Int? = null,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    addBookshelfViewModel: AddBookshelfViewModel = hiltViewModel(),
){
    val state by addBookshelfViewModel.addBookshelfState.collectAsState()

    LaunchedEffect(bookshelfId) {
        addBookshelfViewModel.onBookshelfIdPassed(bookshelfId)
    }
    //TODO: Error handling
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(paddingValues)
            .fillMaxSize()
    ){
        BookshelfForm(
            name = state.name,
            onNameChange = {
                addBookshelfViewModel.onNameChange(it)
            },
            description = state.description,
            onDescriptionChange = {
                addBookshelfViewModel.onDescriptionChange(it)
            },
            onAddBookshelf = {
                addBookshelfViewModel.onSubmit(onNavigateBack)
            },
            bookshelfType = state.bookshelfType,
            onBookshelfTypeChange = {
                addBookshelfViewModel.onBookshelfTypeChange(it)
            },
            bookshelfId = bookshelfId,
            isAddButtonEnabled = addBookshelfViewModel.isAddButtonEnabled()
        )
    }



}

@Composable
fun BookshelfForm(
    modifier: Modifier = Modifier,
    name: String,
    bookshelfId: Int?,
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
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        FormTextField(
            value = name,
            onValueChange = {
                onNameChange(it)
            },
            label = ("Name"),

        )
        FormTextField(
            value = description,
            onValueChange = {
                onDescriptionChange(it)
            },
            label = "Description",

        )

        Spacer(modifier = modifier.padding(24.dp))

        Button(
            enabled = isAddButtonEnabled,
            onClick = {onAddBookshelf()},
            modifier = modifier.fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                    contentDescription = "Add Bookshelf"
                )

                Text(text = if (bookshelfId == null) "Add Bookshelf" else "Edit Bookshelf")
            }


        }
    }
}

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String
){
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
}