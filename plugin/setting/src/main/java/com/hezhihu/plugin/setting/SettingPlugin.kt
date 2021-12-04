package com.hezhihu.plugin.setting

import com.hezhihu.gradle.plugin.base.Framework
import com.hezhihu.gradle.plugin.base.GitInfo
import com.hezhihu.gradle.plugin.base.Module
import com.hezhihu.gradle.plugin.base.appFrameworkFromFile
import com.hezhihu.plugin.setting.repo.GitUtils
import freemarker.template.Template
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.*

class SettingPlugin: Plugin<Settings>{

    override fun apply(settings: Settings) {
        settings.run {
            getDependenciesFile().apply {
                appFrameworkFromFile(this).apply {
                    app.framework.forEach{ group ->
                        includeGroup(group)
                        group.modules.forEach { module ->
                            includeModule(module,group)
                        }
                    }
                }
            }
        }
    }

    /**
     * 集成group
     */
    private fun Settings.includeGroup(framework: Framework){
        val id = framework.id
        val group = framework.group
        val path = framework.path
        File(rootDir,path).run {
            val gitInfo = framework.git
            if(null != gitInfo){
                handlerModuleGitDir(this@includeGroup,gitInfo)
            }else{
                createLibraryDir(id,group,this)
            }
            include(":$id")
            project(":$id").projectDir = this
        }
    }

    private fun File.handlerModuleGitDir(settings: Settings,gitInfo: GitInfo){
        if(exists()){
            GitUtils.removeCachedDir(settings.rootProject.projectDir, this.canonicalPath)
            val moduleGitInitialized = GitUtils.isGitDir(this)
            if(moduleGitInitialized){
                setRemote(this,gitInfo)
                val branch = gitInfo.branch
                val currentBranch = GitUtils.getBranchName(this)
                println("当前 currentBranch $currentBranch")
                if(branch != currentBranch){
                    val isClean = GitUtils.isClean(this)
                    if(isClean){
                        if(GitUtils.isLocalBranch(this,branch)){
                            GitUtils.checkoutBranch(this,branch)
                            println("[repo] - module '$this': git checkout $branch")
                        }else{
                            if(GitUtils.isRemoteBranch(this,branch)){
                                GitUtils.checkoutRemoteBranch(this,branch)
                                println("[repo] - module '$this': git checkout -b $branch origin/$branch")
                            }else{
                                GitUtils.checkoutNewBranch(this,branch)
                                println("[repo] - module '$this': git checkout -b $branch")
                            }
                        }
                    }
                }
            }else{
                val originUrl = gitInfo.fetch
                if(list().isNotEmpty()){
                    GitUtils.init(this)
                    GitUtils.addRemote(this,originUrl)

                }else{
                    println("[repo] - module ${this.name}': git clone $originUrl --branch ${gitInfo.branch}")
                    GitUtils.clone(this,originUrl,gitInfo.branch)
                }
                if(null != gitInfo.push && gitInfo.push != originUrl){
                    GitUtils.setOriginRemotePushUrl(this, gitInfo.push!!)
                }
                GitUtils.addExclude(this)
            }
        }else{
            ///文件不存在就创建一个文件夹
            mkdirs()
            val originUrl = gitInfo.fetch
            val pushUrl = gitInfo.push ?: originUrl
            val branch = gitInfo.branch
            println("[repo] - module '$': git clone $originUrl --branch $branch")
            GitUtils.clone(this, originUrl, branch)
            if(originUrl != pushUrl){
                GitUtils.setOriginRemotePushUrl(this, pushUrl)
            }
            GitUtils.addExclude(this)
        }
    }

    /**
     * 集成自模块
     */
    private fun Settings.includeModule(module: Module,frameworkGroup: Framework){
        val id = module.id
        val groupId = frameworkGroup.id
        val group = frameworkGroup.group
        val path = "${frameworkGroup.path}${File.separator}${module.path}"
        File(rootDir,path).run {
            val gitInfo = module.git
            if(null != gitInfo){
                handlerModuleGitDir(this@includeModule,gitInfo)
            }else {
                createLibraryDir(id, group, this)
            }
            val include = ":$groupId:$id"
            include(include)
            project(include).projectDir = this
        }
    }

    /**
     * 获取依赖文件
     */
    private fun Settings.getDependenciesFile(): File {
        val file = File(rootProject.projectDir, "dependencies.json")
        if (!file.exists()) {
            println("没有文件")
            SettingPlugin::class.java.getResource("/source/dependencies.json")?.readText()?.apply {
                println("写入文件：${this}")
                FileWriter(file).run {
                    write(this@apply)
                    flush()
                    close()
                }
            }
        }
        return file
    }

    /**
     * 创建文件模块路径
     */
    private fun Settings.createLibraryDir(moduleID: String,moduleGroup: String,modulePath: File){
        if(!modulePath.exists()){
            ////创建根路径
            modulePath.mkdir()
            val configuration = TemplateLoader().configuration

            modulePath.apply{///创建gradle 文件

                File(this,"libs").mkdir()
                createFileFromTemplate(File(this,"build.gradle"), mapOf(),configuration.getTemplate(TemplateLoader.GRADLE))
                createFileFromTemplate(File(this,".gitignore"), mapOf(),configuration.getTemplate(TemplateLoader.IGNORE))
                createFileFromTemplate(File(this,"consumer-rules.pro"), mapOf(),configuration.getTemplate(TemplateLoader.CONSUMER))
                createFileFromTemplate(File(this,"proguard-rules.pro"), mapOf(),configuration.getTemplate(TemplateLoader.PROGUARD))
            }.let {///创建main 文件夹

                val mainPath = File(it,"src/main/")
                mainPath.mkdirs()
                createFileFromTemplate(File(mainPath,"AndroidManifest.xml"), mapOf("packageName" to "$moduleGroup.$moduleID"),configuration.getTemplate(TemplateLoader.MANIFEST))
                mainPath
            }.apply { ///创建java 文件夹
                val javaPath = File(this,"java/${moduleGroup.replace(".","/")}.$moduleID")
                javaPath.mkdirs()

            }.run {

                val resourcesPath = File(this,"res")
                val valuesPath = File(resourcesPath,"values")
                valuesPath.mkdirs()
                createFileFromTemplate(File(valuesPath,"colors.xml"), mapOf(),configuration.getTemplate(TemplateLoader.COLORS))
                createFileFromTemplate(File(valuesPath,"styles.xml"), mapOf(),configuration.getTemplate(TemplateLoader.STYLES))

                resourcesPath
            }.apply {///创建图片文件夹

                val drawables = arrayOf("drawable","drawable-hdpi","drawable-mdpi","drawable-xhdpi","drawable-xxhdpi","drawable-xxxhdpi")
                drawables.forEach {
                    File(this,it).mkdir()
                }
            }
        }

    }

    /**
     * 从模板中创建文件
     */
    private fun Settings.createFileFromTemplate(toPath: File,data: Map<String,String>,template: Template){
        val out: Writer = BufferedWriter(
            OutputStreamWriter(
                FileOutputStream(toPath), "UTF-8"
            )
        )
        template.process(data,out)
        out.flush()
        out.close()
    }

    fun setRemote(dir: File,gitInfo: GitInfo?){
        if(null == gitInfo){
            GitUtils.removeRemote(dir)
            return
        }

        val fetchUrl = GitUtils.getOriginRemoteFetchUrl(dir)
        if(fetchUrl == null){
            GitUtils.addRemote(dir,gitInfo.fetch)
        }else if(gitInfo.fetch != fetchUrl){
            GitUtils.setOriginRemoteUrl(dir,gitInfo.fetch)
        }

        val pushUrl = GitUtils.getOriginRemotePushUrl(dir)
        if(null != pushUrl && pushUrl != gitInfo.push){
            GitUtils.setOriginRemotePushUrl(dir,pushUrl)
        }
    }

}