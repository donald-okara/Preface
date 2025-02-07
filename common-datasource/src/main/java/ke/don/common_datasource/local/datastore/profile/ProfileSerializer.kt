package ke.don.common_datasource.local.datastore.profile

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import ke.don.shared_domain.data_models.Profile
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object ProfileSerializer : Serializer<Profile>{
    override val defaultValue: Profile
        get() = Profile()

    override suspend fun readFrom(input: InputStream): Profile {
        return try {
            Json.decodeFromString(
                deserializer = Profile.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e: SerializationException){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Profile, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = Profile.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

val Context.profileDataStore by dataStore("profile.json", ProfileSerializer)