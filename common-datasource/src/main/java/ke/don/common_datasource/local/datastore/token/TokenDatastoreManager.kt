package ke.don.common_datasource.local.datastore.token

import android.content.Context
import kotlinx.coroutines.flow.first

class TokenDatastoreManager(
    private val context: Context
) {
    suspend fun setToken(token: String?) {
        context.tokenDatastore.updateData {
            it.copy(
                token = token
            )
        }

    }

    suspend fun getToken(): TokenData {
        return context.tokenDatastore.data.first()
    }

}