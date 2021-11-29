package com.hezhihu.gradle.plugin

import com.hezhihu.gradle.plugin.base.appFrameworkFromFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.plugins.BasePluginConvention
import org.gradle.api.plugins.MavenRepositoryHandlerConvention
import org.gradle.api.tasks.Upload
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * 上传 maven 组件功能
 */
class ApplyMavenPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        if(project.rootProject != project){
            throw IllegalArgumentException("请将Plugin 依赖在 rootProject 模块下")
        }

        project.afterEvaluate {
            appFrameworkFromFile(project.rootProject.file("dependencies.json")).app.framework.forEach{ framework ->
                val frameworkId = framework.id
                val frameworkGroup = framework.group
                val frameworkVersion = framework.version
                it.getProject(":$frameworkId")?.afterEvaluate {
                    it.updateMaven(frameworkId,frameworkGroup,frameworkVersion)
                }
                framework.modules.forEach {  module ->
                    val moduleId = module.id
                    val moduleVersion = module.version ?: frameworkVersion
                    it.getProject(":$frameworkId:$moduleId")?.afterEvaluate {
                        it.updateMaven(moduleId,frameworkGroup,moduleVersion)
                    }
                }
            }
        }
    }

    /**
     * 更新 maven 版本
     */
    private fun Project.updateMaven(id: String,group: String,version: String){
        this.group = group
        this.version = version
        this.convention.findPlugin(BasePluginConvention::class.java)?.apply {
            archivesBaseName = id
        }
        addMavenPlugin()
    }

    /**
     * 添加 maven 插件
     */
    private fun Project.addMavenPlugin(){
        apply(hashMapOf("plugin" to "maven"))
        tasks.getByName("uploadArchives"){
            if(it is Upload){
                try {
                    val maven = DslObject(it.repositories).convention.getPlugin(MavenRepositoryHandlerConvention::class.java)
                    val mavenDeploy = maven.mavenDeployer()
                    mavenDeploy.run {
                        val repository = javaClass.getMethod("repository", Map::class.java)
                            .invoke(this, mapOf("url" to "file://${project.rootProject.projectDir.path}/maven/m2"))
                        repository.javaClass.getMethod("authentication", Map::class.java)
                            .invoke(repository, mapOf("userName" to "","password" to ""))

                        val snapshotRepository = javaClass.getMethod("snapshotRepository", Map::class.java)
                            .invoke(this, mapOf("url" to "file://${project.rootProject.projectDir.path}/maven/m2"))
                        snapshotRepository.javaClass.getMethod("authentication", Map::class.java)
                            .invoke(snapshotRepository, mapOf("userName" to "","password" to ""))
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 寻找模块
     */
    private fun Project.getProject(projectId: String): Project?{
        try{
            return rootProject.project(projectId)
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
}