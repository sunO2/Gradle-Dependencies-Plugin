package com.hezhihu.testdemo.dialog

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/main/dialog")
class DialogActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AlertDialog.Builder(this)
            .setTitle("标题提示")
            .setMessage("提示卡 验证测试")
//            .setView(R.layout.text_view_test)
            .setItems(arrayOf("确认","取消")){ dialog,_ ->
                dialog.dismiss()
        }.create().show()

    }

}