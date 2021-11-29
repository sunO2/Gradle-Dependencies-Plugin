package com.hezhihu.testdemo

import android.app.Application
import com.hezhihu.framework.navigation.Navigation

class APP: Application() {

    override fun onCreate() {
        super.onCreate()
        Navigation.Init.init(this)
    }
}