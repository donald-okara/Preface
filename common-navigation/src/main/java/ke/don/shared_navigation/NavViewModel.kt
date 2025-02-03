package ke.don.shared_navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.don.common_datasource.remote.data.di.SupabaseClient
import ke.don.common_datasource.remote.data.repositoryImpl.ProfileRepositoryImpl
import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {
    //val isSignedIn = profileRepository.isSignedIn


}