package com.hezhihu.testdemo.booster

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.hezhihu.testdemo.R
import java.lang.Exception

/**
 * didi booster 功能测试
 * 测试 booster 部分功能
 */
class BoosterActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this))
        ///验证 raw 问题
        boostSourcesRaw()
    }


    /**
     * 验证 booster raw 获取
     * 异常问题
     */
    private fun boostSourcesRaw(){
        Log.d("BoosterActivity","boostSourcesRaw")
        var fd = resources.openRawResourceFd(R.raw.test)
    }
}