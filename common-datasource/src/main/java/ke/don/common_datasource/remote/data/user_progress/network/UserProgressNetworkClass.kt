package ke.don.common_datasource.remote.data.user_progress.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.remote.domain.error_handler.CompositeErrorHandler
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.data_models.UserProgressBookView
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.values.USERPROGRESS
import ke.don.shared_domain.values.USERPROGRESSVIEW

class UserProgressNetworkClass(
    private val supabaseClient: SupabaseClient
) {
    private val errorHandler = CompositeErrorHandler()

    /**
     * CREATE
     */
    suspend fun addUserProgress(
        userProgress: CreateUserProgressDTO
    ): NetworkResult<NoDataReturned>{
        return try {
            supabaseClient.from(USERPROGRESS).insert(userProgress)
            NetworkResult.Success(NoDataReturned())
        }catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    /**
     * READ
     */
    suspend fun fetchBookProgressByUserAndBook(userId: String, bookId: String) : NetworkResult<UserProgressResponse?>{
        return try {
            val result = supabaseClient.from(USERPROGRESS)
                .select {
                    filter {
                        UserProgressResponse::userId eq userId
                        UserProgressResponse::bookId eq bookId
                    }
                }
                .decodeSingleOrNull<UserProgressResponse>()
            NetworkResult.Success(result)
            } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    suspend fun fetchUserProgressBookView(userId: String) : NetworkResult<List<UserProgressBookView>> {
        return try {
            val result = supabaseClient.from(USERPROGRESSVIEW)
                .select {
                    filter { UserProgressBookView::userId eq userId }
                }
                .decodeList<UserProgressBookView>()

            NetworkResult.Success(result)
        }catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }

    }

    suspend fun fetchBookProgressByUser(userId: String) : NetworkResult<List<UserProgressResponse>> {
        return try {
            val result = supabaseClient.from(USERPROGRESS)
                .select {
                    filter { UserProgressResponse::userId eq userId }
                }
                .decodeList<UserProgressResponse>()
            NetworkResult.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    suspend fun fetchBookProgressByBook(bookId: String) : NetworkResult<List<UserProgressResponse>> {
        return try {
            val result = supabaseClient.from(USERPROGRESS)
                .select {
                    filter { UserProgressResponse::bookId eq bookId }
                }
                .decodeList<UserProgressResponse>()
            NetworkResult.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    /**
     * UPDATE
     */
    suspend fun updateUserProgress(userId: String, bookId: String, newCurrentPage: Int) : NetworkResult<NoDataReturned> {
        return try {
            supabaseClient.from(USERPROGRESS).update(
                mapOf("current_page" to newCurrentPage)
            ){
                filter {
                    UserProgressResponse::bookId eq bookId
                    UserProgressResponse::userId eq userId
                }
            }
            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }

    /**
     * DELETE
     */

    suspend fun deleteUserProgress(userId: String, bookId: String) : NetworkResult<NoDataReturned> {
        return try {
            supabaseClient.from(USERPROGRESS).delete {
                filter {
                    UserProgressResponse::bookId eq bookId
                    UserProgressResponse::userId eq userId
                }
            }
            NetworkResult.Success(NoDataReturned())
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler.handleException(e)
        }
    }


    companion object {
        const val TAG = "UserProgressNetworkClient"
    }
}