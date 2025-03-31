package ke.don.common_datasource.remote.domain.repositories

import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.NetworkResult

interface UserProgressRepository {
    suspend fun addUserProgress(userProgress: CreateUserProgressDTO): NetworkResult<NoDataReturned>

    suspend fun fetchBookProgressByUserAndBook(userId: String, bookId: String) : NetworkResult<UserProgressResponse>
    suspend fun fetchBookProgressByUser(userId: String) : NetworkResult<List<UserProgressResponse>>
    suspend fun fetchBookProgressByBook(bookId: String) : NetworkResult<List<UserProgressResponse>>

    suspend fun updateUserProgress(userId: String, bookId: String, userProgress: CreateUserProgressDTO) : NetworkResult<NoDataReturned>

    suspend fun deleteUserProgress(userId: String, bookId: String) : NetworkResult<NoDataReturned>
}