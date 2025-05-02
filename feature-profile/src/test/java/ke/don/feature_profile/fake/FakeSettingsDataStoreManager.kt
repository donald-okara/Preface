package ke.don.feature_profile.fake

import ke.don.common_datasource.local.datastore.user_settings.AppSettings
import ke.don.common_datasource.local.datastore.user_settings.SettingsDataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsDataStoreManager : SettingsDataStoreManager {
    private val settingsFlow = MutableStateFlow(AppSettings(darkTheme = false))
    override suspend fun setSettings(settings: AppSettings) {
        TODO("Not yet implemented")
    }

    override fun getSettings(): Flow<AppSettings> = settingsFlow

}
