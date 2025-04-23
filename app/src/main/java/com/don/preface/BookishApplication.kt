package com.don.preface

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ke.don.common_datasource.local.worker.classes.PrefaceWorkerFactory
import javax.inject.Inject

@HiltAndroidApp
class BookishApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: PrefaceWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}
