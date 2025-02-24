package ke.don.feature_bookshelf.presentation.screens.bookshelf_details.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ke.don.feature_bookshelf.R
import ke.don.feature_bookshelf.presentation.shared_components.BooksCoverStack

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