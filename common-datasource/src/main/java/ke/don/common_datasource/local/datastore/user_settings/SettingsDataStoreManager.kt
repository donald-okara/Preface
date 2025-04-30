package ke.don.common_datasource.local.datastore.user_settings

import android.content.Context
import kotlinx.coroutines.flow.Flow

class SettingsDataStoreManagerImpl(
    private val context: Context
): SettingsDataStoreManager{
    override suspend fun setSettings(settings: Settings) {
        context.settingsDatastore.updateData {
            settings
        }
    }

    override fun getSettings(): Flow<Settings> {
        return context.settingsDatastore.data
    }
}

interface SettingsDataStoreManager{
    suspend fun setSettings(settings: Settings)
    fun getSettings(): Flow<Settings>
}
