package ke.don.feature_profile.fake

import ke.don.common_datasource.local.datastore.user_settings.Settings
import ke.don.common_datasource.local.datastore.user_settings.SettingsDataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDataStoreManager : SettingsDataStoreManager {
    private val settingsFlow = MutableStateFlow(Settings(darkTheme = false))
    override suspend fun setSettings(settings: Settings) {
        TODO("Not yet implemented")
    }

    override fun getSettings(): Flow<Settings> = settingsFlow

}
