package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.common_datasource.local.roomdb.entities.BookshelfEntity
import ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components.BookList
import ke.don.feature_bookshelf.presentation.shared_components.BookshelfOptionsSheet
import ke.don.shared_domain.states.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfDetailsRoute(
    modifier: Modifier = Modifier,
    bookshelfId: Int,
    onNavigateToEdit: (Int) -> Unit,
    bookshelfDetailsViewModel: BookshelfDetailsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val bookshelfUiState by bookshelfDetailsViewModel.bookshelfUiState.collectAsState()
    val bookshelf by bookshelfUiState.bookShelf.collectAsState(initial = BookshelfEntity())

    val showBottomSheet by bookshelfDetailsViewModel.showOptionsSheet.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(bookshelfId) {
        bookshelfDetailsViewModel.onBookshelfIdPassed(bookshelfId)
    }

    DisposableEffect(Unit) {
        onDispose {
            bookshelfDetailsViewModel.onCleared()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { bookshelfDetailsViewModel.updateShowSheet(true) }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Options"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = bookshelfUiState.resultState) {
                is ResultState.Success -> {
                    if (bookshelf.id == -1 || bookshelf.name == "") { // Loading state indicator
                        CircularProgressIndicator()
                    } else {
                        BookList(
                            bookShelf = bookshelf,
                            modifier = modifier,
                            scrollBehavior = scrollBehavior,
                            onItemClick = onItemClick
                        )
                        BookshelfOptionsSheet(
                            modifier = modifier,
                            bookCovers = bookshelf.books.mapNotNull { it.highestImageUrl?.takeIf { it.isNotEmpty() } },
                            title = bookshelf.name,
                            bookshelfSize = "${bookshelf.books.size} books",
                            showBottomSheet = showBottomSheet,
                            onDismissSheet = { bookshelfDetailsViewModel.updateShowSheet(false) },
                            bookshelfId = bookshelfId,
                            onNavigateToEdit = onNavigateToEdit,
                            onDeleteBookshelf = {
                                bookshelfDetailsViewModel.deleteBookshelf(
                                    bookshelfId = bookshelfId,
                                    onNavigateBack = navigateBack
                                )
                            }
                        )
                    }
                }
                is ResultState.Error -> {
                    Text(
                        text = "Error loading bookshelf: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> { // For Loading or Empty states
                    CircularProgressIndicator()
                }
            }
        }
    }
}
