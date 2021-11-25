package com.hezhihu.testdemo.aspectj;

import android.util.Log;

import com.hezhihu.testdemo.MainActivity;
import com.hezhihu.testdemo.dialog.DialogActivity;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AspectjUtils {

//    @Pointcut("call(* com.hezhihu.testdemo.MainActivity.onItemClick(..))")
//    public void test(){}

    /**
     * 处理问题
     * @param proceedingJoinPoint 节点
     */
    @Around("call(* com.hezhihu.testdemo.MainActivity.onItemClick(..))")
    public String fixCrash(ProceedingJoinPoint proceedingJoinPoint){
        Log.d("AspectjUtils","getView");
        try {
            String data = (String) proceedingJoinPoint.proceed();
            Log.d("AspectjUtils","执行结果：" + data);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return "拦截处理方法";
    }

}
