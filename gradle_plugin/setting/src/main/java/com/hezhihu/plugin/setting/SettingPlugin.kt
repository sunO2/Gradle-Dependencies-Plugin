package com.hezhihu.plugin.setting

import com.google.gson.Gson
import freemarker.template.Template
import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.*

class SettingPlugin: Plugin<Settings>{


    override fun apply(settings: Settings) {
        settings.run {

            FileReader(File(rootDir,"dependencies.json")).readText().run {
                val app = Gson().fromJson(this,APPFramework::class.java)
                app.app.framework[0].dependencies.dependencies()
            }

            XmlParser().parse(getDependenciesFile()).run {
                children().forEach { group ->
                    if(group is Node){
                        val groupId = group.attribute("id")
                        val groupPath = group.attribute("path")
                        val groupName = group.attribute("group")
                        includeGroup(groupId as String,groupName as String, groupPath as String)
                        group.children().forEach{ module ->
                            if(module is Node){
                                val childId = module.attribute("id")
                                val childPath = module.attribute("path")
                                includeModule(childId as String,groupId,groupName,"$groupPath${File.separator}$childPath")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 集成group
     */
    private fun Settings.includeGroup(id: String,group: String,path: String){
        File(rootDir,path).run {
            createLibraryDir(id,group,this)
            include(":$id")
            project(this)
        }
    }

    /**
     * 集成自模块
     */
    private fun Settings.includeModule(id: String,groupId: String,group: String,path: String){
        File(rootDir,path).run {
            createLibraryDir(id,group,this)
            include(":$groupId:$id")
            project(this)
        }
    }

    /**
     * 获取依赖文件
     */
    private fun Settings.getDependenciesFile(): File {
        val file = File(rootProject.projectDir, "dependencies.xml")
        if (!file.exists()) {
            println("没有文件")
            SettingPlugin::class.java.getResource("/source/dependencies.xml")?.readText()?.apply {
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

            fun createGradleFile(){

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
}