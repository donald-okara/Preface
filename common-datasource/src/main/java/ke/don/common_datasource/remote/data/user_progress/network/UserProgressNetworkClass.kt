package ke.don.common_datasource.remote.data.user_progress.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.shared_domain.data_models.CreateUserProgressDTO
import ke.don.shared_domain.data_models.UserProgressResponse
import ke.don.shared_domain.states.NetworkResult
import ke.don.shared_domain.values.USERPROGRESS

class UserProgressNetworkClass(
    private val supabaseClient: SupabaseClient
) {
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
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
            NetworkResult.Error(message = e.message.toString(), hint = e.cause.toString(), details = e.stackTrace.toString())
        }
    }


    companion object {
        const val TAG = "UserProgressNetworkClient"
    }
}