package ke.don.common_datasource.local.datastore.token

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object TokenSerializer : Serializer<TokenData>{
    override val defaultValue: TokenData
        get() = TokenData()

    override suspend fun readFrom(input: InputStream): TokenData {
        return try {
            Json.decodeFromString(
                deserializer = TokenData.serializer(),
                string = input.readBytes().decodeToString()
            )
        }catch (e: SerializationException){
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: TokenData, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = TokenData.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}

val Context.tokenDatastore by dataStore("token.json", TokenSerializer)


@Serializable
data class TokenData(
    val token: String? = null
)