package com.don.preface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequestBuilder
import com.don.preface.ui.theme.PrefaceTheme
import dagger.hilt.android.AndroidEntryPoint
import ke.don.shared_components.mbuku_theme.ui.theme.MbukuTheme
import ke.don.shared_navigation.AppNavigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MbukuTheme  {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()

                }
            }
        }
    }
}
