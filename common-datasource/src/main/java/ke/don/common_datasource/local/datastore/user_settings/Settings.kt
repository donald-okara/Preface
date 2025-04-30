package ke.don.common_datasource.local.datastore.user_settings

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val darkTheme: Boolean = false
)
