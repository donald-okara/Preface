package ke.don.feature_profile.fake

import ke.don.common_datasource.remote.domain.usecases.ProfileTabUseCases
import ke.don.feature_profile.fake.FakeProfileDetails.fakeProfileDetails
import ke.don.feature_profile.fake.FakeProfileDetails.fakeUserProgress
import ke.don.shared_domain.data_models.ProfileDetails
import ke.don.shared_domain.data_models.UserProgressBookView

class FakeProfileUseCases: ProfileTabUseCases {
    override suspend fun fetchProfileDetails(userId: String): ProfileDetails? {
        println("Inside fake use case:: $userId")

        return if (userId != "null"){
            fakeProfileDetails
        }else{
            null
        }
    }

    override suspend fun fetchProfileProgress(userId: String): List<UserProgressBookView>? {
        return if (userId != "null"){
            listOf(fakeUserProgress)
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