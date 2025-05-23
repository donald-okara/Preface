package ke.don.feature_book_details.fake.data

import ke.don.shared_domain.data_models.BookItem
import ke.don.shared_domain.data_models.BookListItemResponse
import ke.don.shared_domain.data_models.ImageLinks
import ke.don.shared_domain.data_models.VolumeInfo
import retrofit2.Response

object FakeBooksDataSource {

    val fakeBookListItemResponse: List<BookItem> =
       listOf(
           BookItem(
               id = "1",
               volumeInfo = VolumeInfo(
                   title = "The Art of Coding",
                   authors = listOf("Alice Johnson"),
                   publisher = "Tech Books Publishing",
                   publishedDate = "2020-09-15",
                   description = "A comprehensive guide to mastering the art of coding, from basics to advanced techniques.",
                   imageLinks = ImageLinks(
                       smallThumbnail = "https://example.com/image1_small.jpg",
                       thumbnail = "https://example.com/image1_thumb.jpg",
                       small = "https://example.com/image1_small.jpg",
                       medium = "https://example.com/image1_medium.jpg",
                       large = "https://example.com/image1_large.jpg",
                       extraLarge = "https://example.com/image1_extra_large.jpg"
                   ),
                   previewLink = "https://example.com/book1_preview"
               )
           ),
           BookItem(
               id = "2",
               volumeInfo = VolumeInfo(
                   title = "Journey Through Data",
                   authors = listOf("Michael Reed", "Sophia Chen"),
                   publisher = "Data Insights Inc.",
                   publishedDate = "2018-06-23",
                   description = "An exploration of data science and analytics, covering key concepts, techniques, and applications.",
                   imageLinks = ImageLinks(
                       smallThumbnail = "https://example.com/image2_small.jpg",
                       thumbnail = "https://example.com/image2_thumb.jpg",
                       small = "https://example.com/image2_small.jpg",
                       medium = "https://example.com/image2_medium.jpg",
                       large = "https://example.com/image2_large.jpg",
                       extraLarge = "https://example.com/image2_extra_large.jpg"
                   ),
                   previewLink = "https://example.com/book2_preview"
               )
           )
       )

}