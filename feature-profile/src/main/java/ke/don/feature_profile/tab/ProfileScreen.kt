package ke.don.feature_profile.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import ke.don.feature_profile.R
import ke.don.shared_components.ConfirmationDialog
import ke.don.shared_components.DialogType
import ke.don.shared_components.EmptyScreen
import ke.don.shared_components.ProfileStatCard
import ke.don.shared_components.SheetOptionItem
import ke.don.shared_components.ShimmerEffect
import ke.don.shared_domain.states.ProfileTabState
import ke.don.shared_domain.states.ResultState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToSignIn: () -> Unit,
    viewModel: ProfileViewModel,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit,
    profileState: ProfileTabState
){
    LaunchedEffect(viewModel) {
        profileTabEventHandler(ProfileTabEventHandler.FetchProfile)
        profileTabEventHandler(ProfileTabEventHandler.FetchUserProgress)
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.padding(paddingValues)
    ){
        when(profileState.profileResultState){
            is ResultState.Success -> {
                ProfileScreenContent(
                    modifier = modifier,
                    state = profileState,
                    profileTabEventHandler = profileTabEventHandler
                )

                ProfileBottomSheet(
                    modifier = modifier,
                    onSignOut = { viewModel.signOut(onNavigateToSignIn) },
                    state = profileState,
                    profileTabEventHandler = profileTabEventHandler
                )
            }
            is ResultState.Empty -> {
                EmptyScreen(
                    icon = Icons.Default.Person,
                    message = stringResource(R.string.no_profile_message),
                    action = onNavigateToSignIn,
                    actionText = stringResource(R.string.sign_in)

                )
            }

            else -> {
                CircularProgressIndicator(
                    modifier = modifier.align(Alignment.Center)
                )
            }
        }

    }
}

@Composable
fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileTabState,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        ProfileHeader(
            modifier = modifier,
            state = state,
            profileTabEventHandler = profileTabEventHandler
        )

    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    state: ProfileTabState,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation()
        ) {
            Row(
                modifier = modifier.padding(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = state.profile.avatarUrl,
                        contentDescription = "Book cover",
                        contentScale = ContentScale.Crop,
                        modifier = modifier.fillMaxSize(),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(state.profile.name, style = MaterialTheme.typography.titleMedium)
                    Text(state.profile.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = {
                        profileTabEventHandler(ProfileTabEventHandler.ShowBottomSheet)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            ProfileStatCard(label = "Books Read", value = state.userProgress.size.toString(), modifier = Modifier.weight(1f))
            ProfileStatCard(label = "Discovered", value = state.profile.discoveredBooks.toString(), modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun ProfileProgressComponent(){

}

//@Composable
//fun ProfileHeader(
//    modifier: Modifier = Modifier,
//    state: ProfileTabState,
//    profileTabEventHandler: (ProfileTabEventHandler) -> Unit
//){
//    val isPrivate = true // FIXME:
//    Row(
//        modifier = modifier
//            .padding(8.dp)
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Start
//    ) {
//        ProfilePicture(
//            size = 128.dp,
//            profileUrl = state.profile.avatarUrl
//        )
//
//        Column(
//            modifier = modifier
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(
//                text = state.profile.name,
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.Bold
//            )
//
//            if(!isPrivate || state.profile.email.isNotEmpty()){
//                Text(
//                    text = state.profile.email,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//            Text(
//                text = stringResource(R.string.discovered_books, state.profile.discoveredBooks),
//                style = MaterialTheme.typography.titleSmall,
//                fontWeight = FontWeight.Thin
//            )
//        }
//
//        IconButton(
//            onClick = {
//                profileTabEventHandler(ProfileTabEventHandler.ShowBottomSheet)
//            }
//        ) {
//            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
//        }
//    }
//
//}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileBottomSheet(
    modifier: Modifier = Modifier,
    state: ProfileTabState,
    onSignOut: () -> Unit,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit
){
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    if (state.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                profileTabEventHandler(ProfileTabEventHandler.ShowBottomSheet)
            },
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                stickyHeader {
                    ProfileSheetHeader(
                        profileUrl = state.profile.avatarUrl,
                    )
                }
                item {
                    SheetOptionItem(
                        modifier = modifier,
                        icon = Icons.Outlined.Logout,
                        title = "Log out",
                        onOptionClick = {
                            showSignOutDialog = true
                        }
                    )
                }
                item {
                    SheetOptionItem(
                        modifier = modifier,
                        icon = Icons.Outlined.PersonRemove,
                        title = "Delete profile",
                        onOptionClick = {
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog){
        ConfirmationDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmation = {
                profileTabEventHandler(ProfileTabEventHandler.DeleteUser(onSignOut))
                showDeleteDialog = false
            },
            dialogType = DialogType.DANGER,
            dialogTitle = stringResource(R.string.delete_profile),
            dialogText = stringResource(R.string.delete_profile_message),
            icon = Icons.Outlined.PersonRemove
        )
    }

    if (showSignOutDialog){
        ConfirmationDialog(
            onDismissRequest = { showSignOutDialog = false },
            onConfirmation = {
                onSignOut()
                showSignOutDialog = false
            },
            dialogType = DialogType.WARNING,
            dialogTitle = stringResource(R.string.sign_out),
            dialogText = stringResource(R.string.sign_out_message),
            icon = Icons.Outlined.Logout
        )
    }
}

@Composable
fun ProfileSheetHeader(
    modifier: Modifier = Modifier,
    profileUrl: String,
    title: String = "Profile options",
){
    val imageSize = 80.dp
    val textSize = 18f
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(
                profileUrl = profileUrl,
                size = imageSize
            )

            Spacer(modifier = modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = textSize.sp),
                maxLines = 1
            )
        }
        Spacer(modifier = modifier.height(8.dp))

        HorizontalDivider()

        Spacer(modifier = modifier.height(8.dp))

    }
}

@Composable
fun ProfilePicture(
    modifier: Modifier = Modifier,
    size: Dp,
    profileUrl: String = ""
) {
    AsyncImage(
        model = profileUrl,
        contentDescription = "Profile image",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.profile_placeholder)
    )
}

@Preview
@Composable
fun ProfilePicturePreview(){
    ProfilePicture(
        size = 128.dp,
    )
}