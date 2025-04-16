package ke.don.common_datasource.remote.domain.usecases

import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.states.NetworkResult

class ProfileTabUseCaseClass(
    private val profileRepository: ProfileRepository
): ProfileTabUseCases{

    override suspend fun fetchProfileDetails(userId: String): ProfileDetails? {
        return when(val profile = profileRepository.fetchProfileDetails(userId)){
            is NetworkResult.Error -> {
                null
            }
            is NetworkResult.Success -> {
                profile.data
            }
        }
    }

    override suspend fun signOut(): Boolean {
        val result = profileRepository.signOut()
        return result is NetworkResult.Success
    }

    override suspend fun deleteUser(userId: String): Boolean {
        val result = profileRepository.deleteUser(userId)
        return result is NetworkResult.Success
    }
}


interface ProfileTabUseCases{
    suspend fun fetchProfileDetails(userId: String): ProfileDetails?

    suspend fun signOut(): Boolean

    suspend fun deleteUser(userId: String): Boolean

}