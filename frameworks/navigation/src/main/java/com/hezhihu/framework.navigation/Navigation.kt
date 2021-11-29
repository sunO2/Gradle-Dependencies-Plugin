package com.hezhihu.framework.navigation

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

class Navigation {

    companion object Init{
        fun init(application: Application){
            ARouter.openDebug()
            ARouter.openLog()
            ARouter.printStackTrace()
            ARouter.init(application)

        }
    }
}