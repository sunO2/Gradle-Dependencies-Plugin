package com.hezhihu.testdemo.aspectj;

        import android.util.Log;

public class StaticClassTest {

    public void invoke(){
        StaticClassTest.invokeStatic();
    }

    /**
     * test
     */
    private static String invokeStatic(){
        Log.d("StaticClassTest","静态测试");
        return "原始返回数据";
    }

}
