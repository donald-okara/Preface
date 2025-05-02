package ke.don.common_datasource.remote.domain.error_handler

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import ke.don.shared_domain.states.ErrorCategory
import ke.don.shared_domain.states.NetworkResult
import kotlin.reflect.KClass

interface ErrorHandler {
    fun handleException(e: Exception): NetworkResult.Error
}

object RestExceptionHandler : ErrorHandler {
    override fun handleException(e: Exception): NetworkResult.Error {
        val restException = e as RestException
        val (message, code) = when (restException.statusCode) {
            400 -> "Invalid bookshelf data" to "BAD_REQUEST"
            409 -> "Bookshelf already exists" to "CONFLICT"
            401 -> "Authentication error" to "UNAUTHORIZED"
            403 -> "Permission denied" to "FORBIDDEN"
            else -> "Failed to create bookshelf: ${restException.message ?: "Unknown error"}" to "UNKNOWN"
        }
        return NetworkResult.Error(
            message = message,
            details = restException.toString(),
            hint = "Check your input and try again",
            code = code,
            category = ErrorCategory.API
        )
    }
}

object TimeoutExceptionHandler : ErrorHandler {
    override fun handleException(e: Exception): NetworkResult.Error {
        return NetworkResult.Error(
            message = "Request timed out",
            hint = "Check your internet and try again",
            details = e.message.orEmpty(),
            category = ErrorCategory.TIMEOUT
        )
    }
}

object NetworkExceptionHandler : ErrorHandler {
    override fun handleException(e: Exception): NetworkResult.Error {
        return NetworkResult.Error(
            message = "Network error",
            hint = "Check your internet and try again",
            details = e.message.orEmpty(),
            category = ErrorCategory.NETWORK
        )
    }
}

object DefaultExceptionHandler : ErrorHandler {
    override fun handleException(e: Exception): NetworkResult.Error {
        return NetworkResult.Error(
            message = "Unknown error",
            details = e.stackTraceToString(),
            category = ErrorCategory.UNKNOWN

        )
    }
}

class CompositeErrorHandler : ErrorHandler {
    private val handlers: Map<KClass<out Exception>, ErrorHandler> = mapOf(
        RestException::class to RestExceptionHandler,
        HttpRequestTimeoutException::class to TimeoutExceptionHandler,
        HttpRequestException::class to NetworkExceptionHandler
    )

    override fun handleException(e: Exception): NetworkResult.Error {
        val handler = handlers[e::class] ?: DefaultExceptionHandler
        return handler.handleException(e)
    }
}

