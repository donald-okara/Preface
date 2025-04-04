package ke.don.common_datasource.remote.data.user_progress.repositoryImpl

import android.content.Context
import android.widget.Toast
import ke.don.common_datasource.remote.data.user_progress.network.UserProgressNetworkClass
import ke.don.common_datasource.remote.domain.repositories.UserProgressRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.NetworkResult

class UserProgressRepositoryImpl(
    private val userProgressNetworkClass: UserProgressNetworkClass,
    private val context : Context
): UserProgressRepository {
    override suspend fun addUserProgress(userProgress: CreateUserProgressDTO): NetworkResult<NoDataReturned> {
        return userProgressNetworkClass.addUserProgress(userProgress).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun fetchBookProgressByUserAndBook(
        userId: String,
        bookId: String
    ): NetworkResult<UserProgressResponse?> {
        return userProgressNetworkClass.fetchBookProgressByUserAndBook(userId = userId, bookId =bookId).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun fetchBookProgressByUser(userId: String): NetworkResult<List<UserProgressResponse>> {
        return userProgressNetworkClass.fetchBookProgressByUser(userId).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun fetchBookProgressByBook(bookId: String): NetworkResult<List<UserProgressResponse>> {
        return userProgressNetworkClass.fetchBookProgressByBook(bookId).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun updateUserProgress(
        userId: String,
        bookId: String,
        newCurrentPage: Int
    ): NetworkResult<NoDataReturned> {
        return userProgressNetworkClass.updateUserProgress(userId = userId, bookId = bookId, newCurrentPage = newCurrentPage).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun deleteUserProgress(
        userId: String,
        bookId: String
    ): NetworkResult<NoDataReturned> {
        return userProgressNetworkClass.deleteUserProgress(userId = userId, bookId = bookId).also { result ->
            if (result is NetworkResult.Error){
                Toast.makeText(context, "${result.message} ${result.hint}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}