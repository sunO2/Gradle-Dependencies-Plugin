package com.hezhihu.framework.utils

import android.app.Application
import com.blankj.utilcode.util.Utils

class APPUtils{

    companion object Init{
        fun init(application: Application){
            Utils.init(application)
        }
    }

}