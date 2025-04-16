package ke.don.feature_profile.fake

import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCases
import ke.don.feature_profile.fake.FakeProfileDetails.fakeProfileDetails
import ke.don.shared_domain.data_models.ProfileDetails

class FakeProfileUseCases: ProfileTabUseCases {
    override suspend fun fetchProfileDetails(userId: String): ProfileDetails? {
        return if (userId == "null"){
            fakeProfileDetails
        }else{
            null
        }
    }

    override suspend fun signOut(): Boolean {
        return true
    }

    override suspend fun deleteUser(userId: String): Boolean {
        return userId != "null"
    }
}