package com.don.preface.presentation.utils.logger

class LoggerImpl : Logger {
    override fun logDebug(tag: String, message: String) {
        println("$tag DEBUG: $message")
    }

    override fun logError(tag: String, message: String) {
        println("$tag ERROR: $message")
    }

}