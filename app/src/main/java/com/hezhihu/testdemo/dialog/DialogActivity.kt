package com.hezhihu.testdemo.dialog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.hezhihu.testdemo.R

@Route(path = "/main/dialog")
class DialogActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActionSheet.Builder(this).addMenu("切换账号"){

        }.addMenu("退出登陆"){

        }.setCancelText("取消")
            .create().show()

//        AlertDialog.Builder(this)
//            .setTitle("标题提示")
//            .setMessage("提示卡 验证测试")
////            .setView(R.layout.text_view_test)
//            .setItems(arrayOf("确认","取消")){ dialog,_ ->
//                dialog.dismiss()
//        }.create().show()

    }

}