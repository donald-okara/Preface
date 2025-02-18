package ke.don.feature_book_details.fake.network

import ke.don.shared_domain.data_models.BookDetailsResponse
import ke.don.shared_domain.data_models.BookListItemResponse
import ke.don.feature_book_details.fake.data.FakeBookDetailsDataSource
import ke.don.feature_book_details.fake.data.FakeBooksDataSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeBookApiService :
    ke.don.common_datasource.remote.data.book_details.network.GoogleBooksApi {
    override suspend fun searchBooks(
        query: String,
        apiKey: String
    ): Response<BookListItemResponse> {
        return FakeBooksDataSource.fakeBookList
    }

    override suspend fun getBookDetails(
        bookId: String,
        apiKey: String
    ): Response<BookDetailsResponse> {
        return if (bookId == "errorExample") {
            val errorBody = """{"error": "Book not found"}"""
                .toResponseBody("application/json".toMediaTypeOrNull())
            Response.error(404, errorBody)
        } else {
            Response.success(FakeBookDetailsDataSource.fakeBookDetailsResponse)
        }
    }

}