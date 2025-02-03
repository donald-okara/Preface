package com.don.preface

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ke.don.common_datasource.data.di.UserManager

@HiltAndroidApp
class BookishApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        //UserManager.fetchUserProfile()

    }
}