package com.hezhihu.testdemo.booster

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.hezhihu.testdemo.R
import java.lang.Exception

/**
 * didi booster 功能测试
 * 测试 booster 部分功能
 */
@Route(path = "/main/booster")
class BoosterActivity: AppCompatActivity(){

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).apply {
            text = "Booster测试页面"
        })
    }

}