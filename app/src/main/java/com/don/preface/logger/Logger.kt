package com.don.preface.logger

import android.util.Log

interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}

class TestLogger : Logger {
    override fun d(tag: String, message: String) {
        // No-op for testing or print to console if desired
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        // No-op for testing
    }
}
