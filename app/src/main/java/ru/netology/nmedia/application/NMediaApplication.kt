package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//class App : Application() {
//
//    override fun onCreate() {
//        super.onCreate()
//        DependencyContainer.initApp(this)
//    }
//}
@HiltAndroidApp
class App : Application()