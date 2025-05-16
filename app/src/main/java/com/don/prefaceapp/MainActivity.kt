package com.don.prefaceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ke.don.common_datasource.local.datastore.user_settings.AppSettings
import ke.don.common_datasource.local.datastore.user_settings.SettingsDataStoreManager
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme
import ke.don.shared_navigation.AppNavigation
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStoreManager: SettingsDataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsDataStoreManager.getSettings()
                .collectAsState(initial = AppSettings()) // <- you may want a default

            MbukuTheme(
                darkTheme = settings.darkTheme
            )  {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()

                }
            }
        }
    }
}
