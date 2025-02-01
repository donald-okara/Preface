package ke.don.common_datasource.domain.getters

import java.security.MessageDigest
import java.util.UUID


fun generateNonce(): Pair<String, String>{
    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val nonce = digest.fold("") { str, it -> str + "%02x".format(it) }

    return Pair(rawNonce,nonce)
}