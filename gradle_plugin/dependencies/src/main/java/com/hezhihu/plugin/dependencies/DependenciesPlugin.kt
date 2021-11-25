package com.hezhihu.plugin.dependencies

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginConvention
import java.lang.IllegalArgumentException

/**
 * 组件依赖工具
 */
class DependenciesPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        if(project.rootProject != project){
            throw IllegalArgumentException("请将Plugin 依赖在 rootProject 模块下")
        }
        project.extensions.create("dependenciesConfig",DepConfig::class.java)

        project.afterEvaluate {
            val dependenciesMap = project.extensions.getByType(DepConfig::class.java)
                .dependenciesXml.xmlModule()

            project.subprojects.forEach{ sub ->
                sub.afterEvaluate{
                    appendModule2Host(it,dependenciesMap)
                    dependenciesAAR2Code(it,dependenciesMap)
                }
                applyVersionAndGroup(project,dependenciesMap)
            }
        }



    }

    private fun appendModule2Host(project: Project, dependenciesMap: Modules) {
        val mModules = dependenciesMap.get("com.hezhihu.module")
        mModules?.get(project.name)?.apply {
            dependenciesMap.get("com.hezhihu.frameworks")?.run {
                val dependency =
                    project.dependencies.create("com.hezhihu.frameworks:${id}:${version}")
                println(":${project.name} $id $version 添加framework 模块")

                project.dependencies.add("implementation",dependency)
//                project.configurations.getByName("implementation").apply {
//                    dependencies.add(dependency)
//                }
            }
        }
    }

    private fun applyVersionAndGroup(rootProject: Project, dependenciesMap: Modules){
        dependenciesMap.mGroups.keys.forEach{ group ->
            val groupPath = dependenciesMap.mGroups.get(key = group)?.path
            val groupId = dependenciesMap.mGroups.get(key = group)?.id
            val groupVersion = dependenciesMap.mGroups.get(key = group)?.version.toString()
            val groupModules = dependenciesMap.mGroups.get(key = group)?.mModules
            updateVersion(rootProject,":$groupId",group,groupVersion)

            if(null != groupModules && null != groupPath){
                groupModules.keys.forEach { moduleName  ->
                    val moduleId = groupModules[moduleName]?.id.toString()
                    updateVersion(rootProject,":$groupId:$moduleId",group,groupVersion)
                }
            }
        }
    }

    /**
     * 更新项目版本
     */
    private fun updateVersion(rootProject: Project,projectName: String, group: String, version: String){
        rootProject.project(projectName).afterEvaluate{
            it.group = group
            it.version = version
            it.convention.findPlugin(BasePluginConvention::class.java)?.apply {
                archivesBaseName = it.name
            }
        }
    }

    /**
     * 依赖库 转换成 源码
     */
    private fun dependenciesAAR2Code(subJect: Project, dependenciesMap: Modules){
        subJect.afterEvaluate{ it ->
            it.configurations.forEach{
                val depends = it.dependencies
                val size = depends.size - 1
                for(index in size downTo  0){
                    val dependency = depends.elementAt(index)
                    val dependencyGroup = dependency.group
                    if(dependenciesMap.containsKey(dependencyGroup)){
                        dependenciesMap.get(dependencyGroup)?.apply {
                            val dependencyName = dependency.name
                            val groupPath = path
                            if(containsKey(dependencyName) || id == dependencyName){
                                val module = get(dependencyName)
                                val dependencyPath =
                                    if (id != dependencyName) module.path else groupPath

                                val projectPath =
                                    if (id != dependencyName) ":$groupPath:$dependencyPath" else ":$groupPath"

                                println("     ++++++++替换模块成功：${projectPath} ")
                                val replaceModule = subJect.dependencies.create(
                                    subJect.rootProject.project(projectPath)
                                )
                                depends.add(replaceModule)
                                depends.remove(dependency)
                            }
                        }

                    }
                }
                println("准备替换模块后：${depends}")
            }
        }
    }
}