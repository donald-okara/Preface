package ke.don.feature_profile.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import ke.don.feature_profile.R
import ke.don.shared_components.ConfirmationDialog
import ke.don.shared_components.DialogType
import ke.don.shared_components.SheetOptionItem
import ke.don.shared_components.ShimmerEffect
import ke.don.shared_domain.states.ResultState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToSignIn: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
){
    val profileState by viewModel.profileState.collectAsState()

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.padding(paddingValues)
    ){
        when(profileState.resultState){
            is ResultState.Success -> {
                ProfileHeader(
                    modifier = modifier,
                    profileUrl = profileState.profile.avatarUrl,
                    name = profileState.profile.name,
                    email = profileState.profile.email,
                    discoveredBooks = profileState.profile.discoveredBooks,
                    onShowOptionsSheet = viewModel::updateShowSheet
                )

                ProfileBottomSheet(
                    modifier = modifier,
                    profileUrl = profileState.profile.avatarUrl,
                    showBottomSheet = profileState.showBottomSheet,
                    onDeleteProfile = { viewModel.deleteUser(onNavigateToSignIn) },
                    onSignOut = { viewModel.signOut(onNavigateToSignIn) },
                    onDismissSheet = viewModel::updateShowSheet
                )
            }

            else -> {
                ProfileHeaderLoading(
                    modifier = modifier
                )
            }
        }

    }
}

@Composable
fun ProfileHeaderLoading(
    modifier: Modifier = Modifier
) {
    val alphaValue = 0.3f
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ShimmerEffect(
            modifier = Modifier
                .size(128.dp)
                .alpha(alphaValue)
                .clip(RoundedCornerShape(16.dp)) // Match ProfilePicture shape
                .background(Color.LightGray)
        )

        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(32.dp)
                    .alpha(alphaValue)
                    .width(200.dp) // Adjusted to a reasonable width
                    .clip(RoundedCornerShape(8.dp)) // Match text shimmer shape
                    .background(Color.LightGray)
            )

            ShimmerEffect(
                modifier = Modifier
                    .height(24.dp)
                    .alpha(alphaValue)
                    .width(150.dp) // Adjusted width
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
        }
    }
}


@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    profileUrl : String,
    name: String,
    onShowOptionsSheet: () -> Unit,
    isPrivate: Boolean = false,
    email: String,
    discoveredBooks: Int
){
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ProfilePicture(
            size = 128.dp,
            profileUrl = profileUrl
        )

        Column(
            modifier = modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if(!isPrivate){
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = stringResource(R.string.discovered_books, discoveredBooks),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Thin
            )
        }

        IconButton(
            onClick = {
                onShowOptionsSheet()
            }
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDeleteProfile: () -> Unit,
    profileUrl: String,
    onSignOut: () -> Unit,
    onDismissSheet: () -> Unit,
){
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismissSheet()
            },
            sheetState = sheetState
        ) {
            LazyColumn(
                modifier = modifier.padding(16.dp)
            ) {
                stickyHeader {
                    ProfileSheetHeader(
                        profileUrl = profileUrl,
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
                onDeleteProfile()
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