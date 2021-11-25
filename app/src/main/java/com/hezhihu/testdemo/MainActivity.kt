package com.hezhihu.testdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hezhihu.kotlin.KotlinDemoActivity
import com.hezhihu.testdemo.aspectj.StaticClassTest
import com.hezhihu.testdemo.booster.BoosterActivity
import com.hezhihu.testdemo.dialog.DialogActivity
import com.hezhihu.testdemo.fragment.TestFragment
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    class Item(var name: String,var activity: Class<out Activity>){
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var data = arrayListOf(
            Item("booster",BoosterActivity::class.java),
            Item("Dialog", DialogActivity::class.java),
            Item("Kotlin",KotlinDemoActivity::class.java),
            Item("Rxjava",KotlinDemoActivity::class.java),
            Item("CrashTest",BoosterActivity::class.java)
        )
        TestFragment()
        Retrofit.Builder().baseUrl("http:router.my-nas.icu")
        Toast.makeText(this,packageName,Toast.LENGTH_LONG).show()
        list_item.adapter = object: BaseAdapter(){
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
            startActivity(Intent(this@MainActivity, item.activity))
        }else{
            throw NullPointerException("崩溃测试")
        }
        return "原生方法识别"
    }
}