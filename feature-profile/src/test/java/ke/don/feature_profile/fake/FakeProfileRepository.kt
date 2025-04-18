package ke.don.feature_profile.fake

import ke.don.common_datasource.remote.domain.repositories.ProfileRepository
import ke.don.common_datasource.remote.domain.states.NoDataReturned
import ke.don.feature_profile.fake.FakeProfileDetails.fakeProfile
import ke.don.shared_domain.data_models.Profile
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.states.NetworkResult

class FakeProfileRepository() : ProfileRepository {
    override val rawNonce: String = ""
    override val hashedNonce: String = ""

    override suspend fun signInAndInsertProfile(
        idToken: String,
        displayName: String,
        profilePictureUri: String
    ): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun syncUserProfile(userId: String): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun checkSignedInStatus(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetchProfileFromDataStore(): Profile {
        return fakeProfile
    }

    override suspend fun fetchProfileDetails(userId: String): NetworkResult<ProfileDetails?> {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(userId: String): NetworkResult<NoDataReturned> {
        TODO("Not yet implemented")
    }
}