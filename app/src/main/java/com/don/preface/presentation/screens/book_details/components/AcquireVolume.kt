package com.don.preface.presentation.screens.book_details.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.don.preface.R
import com.don.preface.data.model.BookDetailsResponse
import com.don.preface.presentation.screens.book_details.search
import com.don.preface.ui.theme.BookishTheme
import com.don.preface.ui.theme.RoundedCornerShapeLarge

@Composable
fun AcquireVolume(
    modifier: Modifier = Modifier,
    book: BookDetailsResponse,
){
    val context = LocalContext.current
    val volumeName = book.volumeInfo.title
    val amazonSearchUrl = "https://www.amazon.com/s?k=$volumeName&i=stripbooks"
    val somanamiUrl = "https://www.somanami.co.ke/search-results?q=$volumeName"
    val prestigeUrl = "https://prestigebookshop.com/?s=$volumeName&post_type=product"

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {

        AcquireBookItem(
            image = R.drawable.somanamilogo,
            contentDescription = "Soma nami",
            text = "Search for \"${book.volumeInfo.title}\" in Soma nami",
            onClick = {
                search(
                    url = somanamiUrl,
                    context = context
                )
            }
        )
        AcquireBookItem(
            image = R.drawable.prestigelogo,
            contentDescription = "Prestige bookstore",
            text = "Search for \"${book.volumeInfo.title}\" in Prestige",
            onClick = {
                search(
                    url = prestigeUrl,
                    context = context
                )
            }
        )
        AcquireBookItem(
            image = R.drawable.amazon_buy_logo,
            contentDescription = "Amazon",
            text = "Search for \"${book.volumeInfo.title}\" in Amazon",
            onClick = {
                search(
                    url = amazonSearchUrl,
                    context = context
                )
            }
        )
    }

}

@Composable
fun AcquireBookItem(
    modifier: Modifier = Modifier,
    image : Int,
    contentDescription : String,
    onClick : () -> Unit = {},
    text : String,
){
    ListItem(
        headlineContent = {
            Text(
                text = contentDescription,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = modifier.padding(8.dp)
            )
        },
        supportingContent = {
            Text(
                text = text,
                textAlign = TextAlign.Start,
                maxLines = 2,
            )
        },
        leadingContent = {
            Image(
                painter = painterResource(id = image),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(40.dp)
                    .clip(RoundedCornerShapeLarge)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Preview
@Composable
fun AcquirablePreview(){
    BookishTheme{
        Column {
            AcquireBookItem(
                image = R.drawable.undraw_empty_cart_co35,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

                )
            AcquireBookItem(
                image = R.drawable.amazon,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

                )
            AcquireBookItem(
                image = R.drawable.somanamilogo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

                )
            AcquireBookItem(
                image = R.drawable.prestigelogo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

                )
            AcquireBookItem(
                image = R.drawable.amazon_buy_logo,
                contentDescription = "Buy book",
                text = "https://pixabay.com/images/search/icon%20pdf/",

                )

        }

    }
}