package com.hezhihu.testdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.SizeUtils
import com.hezhihu.testdemo.aspectj.StaticClassTest
import com.hezhihu.testdemo.booster.BoosterActivity
import com.hezhihu.testdemo.dialog.DialogActivity

class MainActivity : AppCompatActivity() {

    class Item(var name: String,var activity: String){
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val data = arrayListOf(
            Item("booster","/main/booster"),
            Item("Dialog", "/main/dialog"),
            Item("home", "/app/home"),
            Item("setting", "/setting/home"),
            Item("navigation", "/navigation/home"),
            Item("CrashTest","crash")
        )

        Toast.makeText(this,packageName,Toast.LENGTH_LONG).show()
        findViewById<ListView>(R.id.list_item).adapter = object: BaseAdapter(){
            @SuppressLint("ViewHolder")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val item = LayoutInflater.from(this@MainActivity).inflate(R.layout.main_activity_item_layout,parent,false)
                item.findViewById<TextView>(R.id.title).text = getItem(position).name
                item.setOnClickListener {
                    Log.d("MainActivity",onItemClick(getItem(position)))
                }

                return item
            }

            override fun getItem(position: Int): Item {
                return data[position]
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun getCount(): Int {
                return data.size
            }

        }
    }

    fun onItemClick(item: Item): String{
        StaticClassTest().invoke()
        if(!TextUtils.equals("CrashTest",item.name)) {
            ARouter.getInstance().build(item.activity).navigation(this)
        }else{
            throw NullPointerException("崩溃测试")
        }
        return "原生方法识别"
    }
}