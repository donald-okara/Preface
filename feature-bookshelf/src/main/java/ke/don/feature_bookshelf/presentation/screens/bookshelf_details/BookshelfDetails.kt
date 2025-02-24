package ke.don.feature_bookshelf.presentation.screens.bookshelf_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ke.don.feature_bookshelf.R
import ke.don.feature_bookshelf.presentation.shared_components.BooksCoverStack
import ke.don.shared_domain.data_models.BookShelf
import ke.don.shared_domain.states.ResultState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfDetailsRoute(
    modifier: Modifier = Modifier,
    bookshelfId : Int,
    bookshelfDetailsViewModel: BookshelfDetailsViewModel = hiltViewModel(),
    navigateBack : () -> Unit
){
    val bookshelfUiState by bookshelfDetailsViewModel.bookshelfUiState.collectAsState()

    LaunchedEffect(bookshelfId) {
        bookshelfDetailsViewModel.onBookshelfIdPassed(bookshelfId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
                    BookshelfDetailsContent(
                        bookshelfInfo = bookshelfUiState.bookShelf,
                        modifier = modifier
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

@Composable
fun BookshelfDetailsContent(
    modifier: Modifier = Modifier,
    bookshelfInfo: BookShelf
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BookshelfHeader(
            coverImages = bookshelfInfo.books.mapNotNull { it.highestImageUrl?.takeIf {image->  image.isNotEmpty() } },
            bookshelfName = bookshelfInfo.supabaseBookShelf.name,
            bookshelfDescription = bookshelfInfo.supabaseBookShelf.description,
            bookshelfSize = "${bookshelfInfo.books.size} books"
        )

    }
}

@Composable
fun BookshelfHeader(
    modifier: Modifier = Modifier,
    coverImages: List<String> = emptyList(),
    bookshelfName: String,
    bookshelfDescription: String,
    bookshelfSize: String
){
    /**
     * Header for the bookshelf details screen
     * Header Image
     * Bookshelf name
     * Description
     * Size
     */

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 32.dp
            ),
            modifier = modifier
                .padding(4.dp)
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if(coverImages.isNotEmpty()){
                BooksCoverStack(
                    imageUrls = coverImages,
                    modifier = modifier
                        .fillMaxSize()
                )
            }else{
                Image(
                    painter = painterResource(R.drawable.bookshelf_placeholder),
                    contentDescription = "Bookshelf item",
                    modifier = modifier
                        .fillMaxWidth()
                        .height(175.dp)
                )
            }
        }

        Text(
            text = bookshelfName,
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier
                .padding(top = 8.dp)
        )

        if (bookshelfDescription.isNotEmpty()){
            Text(
                text = bookshelfDescription,
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier
                    .padding(top = 8.dp)
            )
        }

        Text(
            text = bookshelfSize,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier
                .padding(top = 8.dp)
        )
    }



}