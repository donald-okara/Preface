package ke.don.common_datasource.domain.repositories

import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val nonce : String

}