package com.hezhihu.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
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
        project.subprojects{ subProject ->
            subProject.afterEvaluate{ projectAfter ->
                println("添加 maven 插件功能 $projectAfter")
                projectAfter.apply(hashMapOf("plugin" to "maven"))
                projectAfter.tasks.getByName("uploadArchives"){ it ->
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
        }
    }
}