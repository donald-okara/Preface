package ke.don.feature_bookshelf.presentation.shared_components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.feature_bookshelf.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookshelfOptionsSheet(
    modifier: Modifier = Modifier,
    bookCovers: List<String>,
    title: String,
    bookshelfSize: String,
    showBottomSheet: Boolean,
    onDismissSheet: () -> Unit
){
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismissSheet()
            },
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                stickyHeader {
                    BookshelfSheetHeader(
                        modifier = modifier,
                        bookCovers = bookCovers,
                        title = title,
                        bookshelfSize = bookshelfSize
                    )
                }
                item {
                    BookShelfOptionItem(
                        modifier = modifier,
                        icon = Icons.Outlined.Close,
                        title = "Delete bookshelf",
                        onOptionClick = {}
                    )
                }
            }

        }
    }

}

@Composable
fun BookshelfSheetHeader(
    modifier: Modifier = Modifier,
    bookCovers: List<String>,
    title: String,
    bookshelfSize: String,
){
    val imageSize = 80.dp
    val textSize = 18f
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = modifier
                    .padding(4.dp)
                    .size(imageSize)
            ) {
                if (bookCovers.isNotEmpty()) {
                    BooksCoverStack(imageUrls = bookCovers, modifier = modifier.fillMaxSize())
                } else {
                    Image(
                        painter = painterResource(R.drawable.bookshelf_placeholder),
                        contentDescription = "Bookshelf item",
                        modifier = modifier
                            .fillMaxWidth()
                            .height(imageSize)
                    )
                }
            }

            Spacer(modifier = modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
                    maxLines = 1
                )

                Text(
                    text = bookshelfSize,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.padding(top = 4.dp)
                )
            }
        }
        Spacer(modifier = modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = modifier.height(8.dp))

    }
}

@Composable
fun BookShelfOptionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onOptionClick: () -> Unit
){
    val textSize = 18f

    Row(
        modifier = modifier.clickable {
            onOptionClick()
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = modifier.size(24.dp)
        )
        Spacer(modifier = modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
        )
    }
}