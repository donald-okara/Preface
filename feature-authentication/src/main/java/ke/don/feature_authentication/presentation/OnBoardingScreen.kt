package ke.don.feature_authentication.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import ke.don.feature_authentication.data.animations
import ke.don.feature_authentication.data.descriptions
import ke.don.feature_authentication.data.titles
import ke.don.shared_domain.states.ResultState


@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    onSuccessfulSignIn : () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { animations.size }
    )

    val signInState = viewModel.signInState.collectAsState()
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ){
        Column(
            modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HorizontalPager(
                state = pagerState,
                modifier.wrapContentSize()
            ) { currentPage ->
                Column(
                    Modifier
                        .wrapContentSize()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(
                            animations[currentPage]
                        )
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = 1,
                        modifier = modifier.size(400.dp)
                    )
                    Text(
                        text = titles[currentPage],
                        textAlign = TextAlign.Center,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = descriptions[currentPage],
                        modifier.padding(top = 45.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp

                    )
                }
            }

            LookaheadScope {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PageIndicator(
                        pageCount = animations.size,
                        currentPage = pagerState.currentPage,
                        modifier = modifier.padding(60.dp)
                    )

                    AnimatedVisibility(
                        visible = pagerState.currentPage == 2,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                                ),
                        exit = fadeOut(animationSpec = tween(durationMillis = 200)) +
                                slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                )
                    ) {
                        GoogleSignInButton(
                            modifier = modifier,
                            enabled = signInState.value != ResultState.Loading,
                            onClickAction = {
                                viewModel.onSignInWithGoogle(onSuccessfulSignIn)
                            }
                        )
                    }
                }
            }


        }
        if (signInState.value == ResultState.Loading){
            CircularProgressIndicator()

        }
    }



}


@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        repeat(pageCount){
            IndicatorSingleDot(isSelected = it == currentPage )
        }


    }
}

@Composable
fun IndicatorSingleDot(
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {

    val width = animateDpAsState(targetValue = if (isSelected) 35.dp else 15.dp, label = "")
    Box(modifier = modifier
        .padding(2.dp)
        .height(15.dp)
        .width(width.value)
        .clip(CircleShape)
        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
    )
}


@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onSuccessfulSignIn = {}

    )
}