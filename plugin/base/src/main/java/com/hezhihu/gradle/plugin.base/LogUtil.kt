package com.hezhihu.gradle.plugin.base

import org.gradle.internal.logging.text.StyledTextOutput


class LogUtil(val out: StyledTextOutput, private val header: String, val foot: String) {
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

    fun print(style: StyledTextOutput.Style){
        if(print) {
            append(foot)
            out.style(style).println(log.toString())
        }
        log.clear()
    }
}