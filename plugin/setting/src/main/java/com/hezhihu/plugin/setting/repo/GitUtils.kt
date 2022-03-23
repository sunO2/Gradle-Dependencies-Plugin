package com.hezhihu.plugin.setting.repo

import groovy.lang.Closure
import org.codehaus.groovy.runtime.*
import org.gradle.internal.impldep.org.apache.commons.io.LineIterator
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


class GitUtils {

    companion object{

        private fun String.execute(envp: List<String>?, dir: File): Process{
            return ProcessGroovyMethods.execute(this,envp,dir)
        }

        private fun Process.err(): InputStream {
            return this.errorStream
        }
        
        private fun InputStream.text(): String{
            return IOGroovyMethods.getText(this)
        }

        private fun Process.getText(): String{
            return ProcessGroovyMethods.getText(this)
        }

        private fun String.readLines(): List<String>{
            return StringGroovyMethods.readLines(this as CharSequence)
        }
        
        fun init(dir: File) {
            val process = ("git init").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw RuntimeException("[repo] - failure to execute git command [git init] under ${dir.absolutePath}\nmessage: ${process.err().text()}")
            }
        }

        fun addRemote(dir: File, url: String) {
            val process = ("git remote add origin $url").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote add origin $url] under ${dir.absolutePath}\nmessage: ${process.err().text()}")
            }
        }

        fun removeRemote(dir: File) {
            val fetchUrl = getOriginRemoteFetchUrl(dir)
            if (fetchUrl == null) return

            val process = ("git remote remove origin").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote remove origin] under ${dir.absolutePath}\nmessage: ${process.err().text()}")
            }
        }

        fun clone(dir: File,url: String,branchName: String) {
            val process = ("git clone --branch $branchName $url -l ${dir.name}").execute(null, dir.parentFile)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git clone --branch $branchName $url -l ${dir.name}] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
        }

        fun commit(dir: File, message: String) {
            val process = ("git commit -m \"$message\"").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git commit -m \"$message\"] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
        }

        fun isGitDir(dir: File):Boolean{
            return  File(dir, ".git").exists()
        }

        fun clearGitDir(dir: File) {
            val git =  File(dir, ".git")
            if (git.exists()) {
                git.delete()
            }
        }

        fun removeCachedDir(dir: File,moduleDir: String) {
            val process = ("git rm -r --cached $moduleDir").execute(null, dir)
            process.waitFor()
        }

        fun getOriginRemoteFetchUrl(dir: File): String?{
            val process = ("git remote -v").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote -v] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }

            var url: String? = null
            process.getText().readLines().forEach {
                if (it.startsWith("origin") && it.endsWith("(fetch)")) {
                    url = it.replace("origin", "").replace("(fetch)", "").trim()
                }
            }
            return url
        }

        fun getOriginRemotePushUrl(dir: File):String? {
            val process = ("git remote -v").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote -v] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }

            var url: String? = null
            process.getText().readLines().forEach {
                if (it.startsWith("origin") && it.endsWith("(push)")) {
                    url = it.replace("origin", "").replace("(push)", "").trim()
                }
            }
            return url
        }

        fun setOriginRemoteUrl(dir: File,url: String): Boolean{
            val process = ("git remote set-url origin $url").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote set-url origin $url] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            return process.getText().trim() == ""
        }

        fun setOriginRemotePushUrl(dir: File,url: String):Boolean {
            val process = ("git remote set-url --push origin $url").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git remote set-url --push origin $url] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            return process.getText().trim() == ""
        }

        fun getBranchName(dir: File): String{
            val process = ("git branch").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git branch] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            val lines = process.getText().readLines()
            var branchName: String? = null
            if (lines.isEmpty()) {
                branchName = "master"
            } else {
                lines.forEach {
                    if (it.startsWith("*")) {
                        branchName = it.replace("*", "").trim()
                    }
                }
            }
            if (branchName == null) {
                throw  RuntimeException("[repo] - failure to get git branch name under ${dir.absolutePath}")
            }
            return branchName as String
        }

        fun  isClean(dir: File):Boolean {
            val process = ("git status -s").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git status -s] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            return process.getText().trim() == ""
        }

        fun isLocalBranch(dir: File,branchName: String): Boolean {
            return  File(dir, ".git/refs/heads/$branchName").exists()
        }

        fun isRemoteBranch(dir: File,branchName: String): Boolean {
            var process = ("git fetch").execute(null, dir)
            var result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git fetch] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            var setBranchName = branchName
            if (branchName == "master") {
                setBranchName = "HEAD"
            }

            process = ("git branch -r").execute(null, dir)
            result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git branch -r] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
            return process.getText().contains("origin/$setBranchName")
        }

        fun checkoutBranch(dir: File,branchName: String) {
            val process = ("git checkout $branchName").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git checkout $branchName] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
        }

        fun checkoutRemoteBranch(dir: File,branchName: String) {
            val process = ("git checkout -b $branchName origin/$branchName").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git checkout -b $branchName origin/$branchName] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
        }

        fun checkoutNewBranch(dir: File,branchName: String) {
            val process = ("git checkout -b $branchName").execute(null, dir)
            val result = process.waitFor()
            if (result != 0) {
                throw  RuntimeException("[repo] - failure to execute git command [git checkout -b $branchName] under ${dir.absolutePath}\n message: ${process.err().text()}")
            }
        }

//        fun updateExclude(projectDir: File,repoInfo: RepoInfo) {
//            val ignoreModules =  ArrayList<String>()
//            val includeModules =  ArrayList<String>()
//
//            ignoreModules.add('.repo')
//            ignoreModules.add('.idea/')
//            ignoreModules.add('.iml')
//            ignoreModules.add('*.iml')
//
//            repoInfo.moduleInfoMap.each {
//                val moduleDir = RepoUtils.getModuleDir(projectDir, it.value)
//                val moduleName = RepoUtils.getModuleName(projectDir, moduleDir)
//                String ignoreModule = moduleName.replace(":", "/") + "/"
//                if (ignoreModule.startsWith("/")) {
//                    ignoreModule = ignoreModule.substring(1)
//                }
//
//                if (repoInfo.projectInfo.includeModuleList.contains(it.key)) {
//                    includeModules.add(ignoreModule)
//                } else {
//                    ignoreModules.add(ignoreModule)
//                }
//            }
//
//            File excluvalile =  File(projectDir, '.git/info/exclude')
//            String exclude = ""
//            if (excluvalile.exists()) {
//                excluvalile.eachLine {
//                    val item = it.trim()
//                    if (includeModules.contains(item)) {
//                        return
//                    }
//                    ignoreModules.remove(item)
//                    exclude += item + "\n"
//                }
//            }
//            ignoreModules.each {
//                exclude += it + "\n"
//            }
//            excluvalile.write(exclude)
//        }

        fun addExclude(dir: File) {
            val ignoreList =  ArrayList<String>()
            ignoreList.add("build/")
            ignoreList.add(".iml")
            ignoreList.add("*.iml")

            val excluvalile = File(dir, ".git/info/exclude")
            var exclude = ""
            if (excluvalile.exists()) {
                excluvalile.readLines().forEach {
                    val item = it.trim()
                    ignoreList.remove(item)
                    exclude += item + "\n"
                }
            }
            ignoreList.forEach() {
                exclude += it + "\n"
            }
            ResourceGroovyMethods.write(excluvalile,exclude)
        }
    }
}