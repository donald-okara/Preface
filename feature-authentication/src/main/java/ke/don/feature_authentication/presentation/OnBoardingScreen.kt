package ke.don.feature_authentication.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ke.don.feature_authentication.R
import kotlinx.coroutines.launch


@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val animations = listOf(
        R.raw.animation1,
        R.raw.animation2,
        R.raw.animation3
    )
    val titles = listOf(
        "Build your library",
        "Share bookshelf",
        "Explore books"
    )

    val descriptions = listOf(
        "Organize your favorite books and create a personalized bookshelves.",
        "Easily share your bookshelves with your friends to share the joy of reading.",
        "Discover new books and expand your horizons with our curated suggestions."
    )

    val pagerState = rememberPagerState(
        pageCount = { animations.size }
    )


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
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animations[currentPage]))
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
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

        PageIndicator(
            pageCount = animations.size,
            currentPage = pagerState.currentPage,
            modifier = modifier.padding(60.dp)
        )
    }

    ButtonsSection(
        pagerState = pagerState,
        onSignInWithGoogleClicked = {
            viewModel.onSignInWithGoogle()
        }
)

}

@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onSignInWithGoogleClicked: () -> Unit,
    pagerState: PagerState
) {

    val scope = rememberCoroutineScope()

    Box(modifier = modifier
        .fillMaxSize()
        .padding(30.dp)){
        if (pagerState.currentPage != 2){
            Text(text = "Next",
                modifier = modifier
                    .align(Alignment.BottomEnd)
                    .clickable {

                        scope.launch {
                            val nextPage = pagerState.currentPage +1
                            pagerState.scrollToPage(nextPage)
                        }
                    },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "Back",
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .clickable {
                        scope.launch {
                            val prevPage = pagerState.currentPage -1
                            if (prevPage >= 0){
                                pagerState.scrollToPage(prevPage)
                            }
                        }
                    },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }else{
            GoogleSignInButton(
                modifier = modifier
                    .align(Alignment.BottomCenter),
                onClickAction = onSignInWithGoogleClicked
            )
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
        .background(if (isSelected) Color(0xFFE92F1E) else Color(0x25E92F1E))
    )
}


@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}