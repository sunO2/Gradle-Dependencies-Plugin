package com.hezhihu.testdemo.proguard;

import androidx.annotation.Keep;

public class ProguardEnty {

    private ProguardInner inner1;
    private ProguardInner inner2;
    private ProguardInner inner3;
    private ProguardInner inner4;
    private ProguardInner inner5;
    private ProguardInner inner6;

    @Keep
    public static class ProguardInner{
        private int demo1;
        private int demo2;
        private int demo3;
        private int demo4;
        private int demo5;
    }

}
