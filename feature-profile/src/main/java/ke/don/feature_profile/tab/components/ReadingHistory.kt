package ke.don.feature_profile.tab.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ke.don.shared_components.CurrentlyReadingCard
import ke.don.shared_components.EmptyScreen
import ke.don.shared_components.FinishedBookCard
import ke.don.shared_domain.data_models.UserProgressBookView
import ke.don.shared_domain.data_models.formatDate

@Composable
fun CurrentlyReadingContainer(
    modifier: Modifier = Modifier,
    books: List<UserProgressBookView>,
    onNavigateToBook: (String) -> Unit
) {
    var viewAllClicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(16.dp)
            .heightIn(max = 400.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        CurrentlyReadingSection(
            bookSize = books.size,
            books = if (viewAllClicked) books else books.take(1),
            onViewAllClick = { viewAllClicked = !viewAllClicked },
            isExpanded = viewAllClicked,
            onNavigateToBook = onNavigateToBook
        )
    }


}


@Composable
fun CurrentlyReadingSection(
    books: List<UserProgressBookView>,
    isExpanded: Boolean,
    bookSize: Int,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToBook: (String) -> Unit
) {
    Log.d("ReadingHistory", "books.size = ${books.size}; books = $books")

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Currently Reading",
                style = MaterialTheme.typography.titleMedium
            )
            if (bookSize > 1) {
                Text(
                    text = if (isExpanded) "View Less" else "View All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (bookSize == 0){
            EmptyScreen(
                icon = Icons.Outlined.CollectionsBookmark,
                message = "You haven't started reading any books yet",
                action = {},
                actionText = "",
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val toShow = if (isExpanded) books else books.take(2)
            items(toShow) { book ->
                CurrentlyReadingCard(
                    imageUrl = book.highestImageUrl,
                    currentPage = book.currentPage,
                    totalPages = book.totalPages,
                    title = book.title,
                    lastUpdated = formatDate(book.lastUpdated),
                    modifier = modifier.clickable {
                        onNavigateToBook(book.bookId)
                    }
                )
            }
        }
    }
}

@Composable
fun ReadingHistoryContainer(
    modifier: Modifier = Modifier,
    books: List<UserProgressBookView>,
    onNavigateToBook: (String) -> Unit
) {
    var viewAllClicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(16.dp)
            .heightIn(max = 400.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        ReadingHistorySection(
            books = if (viewAllClicked) books else books.take(1),
            onViewAllClick = { viewAllClicked = !viewAllClicked },
            isExpanded = viewAllClicked,
            onNavigateToBook = onNavigateToBook,
            bookSize = books.size
        )
    }


}


@Composable
fun ReadingHistorySection(
    modifier: Modifier = Modifier,
    books: List<UserProgressBookView>,
    isExpanded: Boolean,
    bookSize: Int,
    onViewAllClick: () -> Unit,
    onNavigateToBook: (String) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reading History",
                style = MaterialTheme.typography.titleMedium
            )
            if(bookSize > 1){
                Text(
                    text = if (isExpanded) "View Less" else "View All",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (bookSize == 0){
            EmptyScreen(
                icon = Icons.Outlined.CollectionsBookmark,
                message = "You haven't completed any books yet",
                action = {},
                actionText = "",
                modifier = modifier.align(Alignment.CenterHorizontally)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(), // <- ensures height matches content
        ) {
            // Show the initial 2 books
            val toShow = if (isExpanded) books else books.take(2)
            items(toShow) { book ->
                FinishedBookCard(
                    imageUrl = book.highestImageUrl,
                    title = book.title,
                    dateCompleted = formatDate(book.lastUpdated),
                    modifier = modifier.clickable {
                        onNavigateToBook(book.bookId)
                    }
                )
            }

        }

    }
}