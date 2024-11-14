package com.don.preface.domain.logger

interface Logger {
    fun logDebug(tag: String, message: String)

    fun logError(tag: String, message: String)
}
