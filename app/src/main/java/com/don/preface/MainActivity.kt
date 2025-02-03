package com.don.preface

import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.don.preface.ui.theme.PrefaceTheme
import dagger.hilt.android.AndroidEntryPoint
import ke.don.common_datasource.data.di.UserManager
import ke.don.shared_domain.values.Screens
import ke.don.shared_navigation.NavGraph
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val userProfile = UserManager.userProfile

        val startDestinationScreen = Screens.OnBoarding.route

        enableEdgeToEdge()
        setContent {
            PrefaceTheme {
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        navController = navController,
                        startDestinationScreen = startDestinationScreen
                    )

                }
            }
        }
    }
}

@Composable
fun PrefaceSplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToMain : () -> Unit
) {
    val scale = remember{
        Animatable(initialValue = 0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(3000L)
        onNavigateToMain()
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.brown))
    ){
        Image(
            painter = painterResource(id = R.drawable.splash_screen_2),
            contentDescription = "Splash Screen",
            modifier = modifier
                .fillMaxSize()
                .scale(scale.value)
        )
    }
}