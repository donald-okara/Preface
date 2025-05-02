package ke.don.common_datasource.local.datastore.user_settings

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val darkTheme: Boolean = false
)
