package com.hezhihu.framework.navigation

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = "/navigation/home")
class NavigationActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            addView(Button(context).apply {
                text = "Home页面"
                this.setOnClickListener {
                    ARouter.getInstance().build("/app/home").navigation()
                }
            })
            addView(Button(context).apply {
                text = "Booster页面"
                this.setOnClickListener {
                    ARouter.getInstance().build("/main/booster").navigation()
                }
            })
            addView(Button(context).apply {
                text = "设置页面"
                this.setOnClickListener {
                    ARouter.getInstance().build("/setting/home").navigation()
                }
            })
        })
    }

}