package com.don.preface

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookishApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        //UserManager.fetchUserProfile()

    }
}