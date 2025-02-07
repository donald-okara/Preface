package ke.don.shared_navigation

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PrefaceSplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToOnBoarding : () -> Unit,
    onNavigateToMain : () -> Unit,
    navViewModel: NavViewModel = hiltViewModel()
) {
    val scale = remember{
        Animatable(initialValue = 0f)
    }
    val isLoading by navViewModel.isLoading.collectAsState()

    val isSignedIn by navViewModel.isSignedIn.collectAsState()

    val coroutineScope = rememberCoroutineScope()

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
    }

    LaunchedEffect(key1 = isSignedIn, key2 = isLoading){
        if (!isLoading) {
            coroutineScope.launch {
                delay(2000L)
                if (isSignedIn) {
                    onNavigateToMain()
                }else{
                    onNavigateToOnBoarding()
                }
            }
        }
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