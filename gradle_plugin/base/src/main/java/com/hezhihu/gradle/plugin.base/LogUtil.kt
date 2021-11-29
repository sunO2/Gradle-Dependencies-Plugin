package com.hezhihu.gradle.plugin.base

class LogUtil(private val header: String, val foot: String) {
    private val log: StringBuilder = StringBuilder()
    private var print = false

    init {
        log.append(header)
        log.appendln()
    }

    fun append(info: String){
        print = true
        log.append(info)
    }

    fun appendln(info: String){
        print = true
        log.append(info)
        log.appendln()
    }

    fun print(){
        if(print) {
            append(foot)
            println(log.toString())
        }
        log.clear()
    }
}