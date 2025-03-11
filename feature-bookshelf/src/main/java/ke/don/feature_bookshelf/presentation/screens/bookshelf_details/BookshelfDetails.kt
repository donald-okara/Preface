package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components.BookList
import ke.don.shared_domain.states.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfDetailsRoute(
    modifier: Modifier = Modifier,
    bookshelfId : Int,
    bookshelfDetailsViewModel: BookshelfDetailsViewModel = hiltViewModel(),
    navigateBack : () -> Unit,
    onItemClick: (String) -> Unit
){
    val bookshelfUiState by bookshelfDetailsViewModel.bookshelfUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(bookshelfId) {
        bookshelfDetailsViewModel.onBookshelfIdPassed(bookshelfId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        ""
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"

                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent, // Transparent background
                )
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            when (bookshelfUiState.resultState) {
                is ResultState.Success -> {
                    BookList(
                        bookShelf = bookshelfUiState.bookShelf,
                        modifier = modifier,
                        scrollBehavior = scrollBehavior,
                        onItemClick = onItemClick
                    )
                }
                is ResultState.Error -> {
                    Text(
                        text = "Error loading bookshelf details",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    CircularProgressIndicator()

                }
            }

        }

    }


}



