package ke.don.feature_profile.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ke.don.feature_profile.R
import ke.don.feature_profile.tab.components.CurrentlyReadingContainer
import ke.don.feature_profile.tab.components.ProfileHeader
import ke.don.feature_profile.tab.components.ProfilePicture
import ke.don.feature_profile.tab.components.ReadingHistoryContainer
import ke.don.shared_components.components.ConfirmationDialog
import ke.don.shared_components.components.DialogType
import ke.don.shared_components.components.EmptyScreen
import ke.don.shared_components.components.SheetOptionItem
import ke.don.shared_domain.states.ProfileTabState
import ke.don.shared_domain.states.ResultState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToSignIn: () -> Unit,
    viewModel: ProfileViewModel,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit,
    profileState: ProfileTabState,
    onNavigateToBook: (String) -> Unit
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
                    profileTabEventHandler = profileTabEventHandler,
                    onNavigateToBook = onNavigateToBook,
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
    onNavigateToBook: (String) -> Unit,
    profileTabEventHandler: (ProfileTabEventHandler) -> Unit
){
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        ProfileHeader(
            modifier = modifier,
            state = state,
            profileTabEventHandler = profileTabEventHandler
        )

        when(state.progressResultState){
            is ResultState.Success -> {
                CurrentlyReadingContainer(
                    books = state.userProgress.filter { it.totalPages != it.currentPage },
                    onNavigateToBook = onNavigateToBook,
                    modifier = modifier
                )

                ReadingHistoryContainer(
                    books = state.userProgress.filter { it.totalPages == it.currentPage },
                    onNavigateToBook = onNavigateToBook,
                    modifier = modifier
                )
            }
            is ResultState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {
                EmptyScreen(
                    icon = Icons.Outlined.Book,
                    message = stringResource(R.string.no_books_message),
                    action = {},
                    actionText = ""

                )
            }

        }

    }
}



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



@Preview
@Composable
fun ProfilePicturePreview(){
    ProfilePicture(
        size = 128.dp,
    )
}