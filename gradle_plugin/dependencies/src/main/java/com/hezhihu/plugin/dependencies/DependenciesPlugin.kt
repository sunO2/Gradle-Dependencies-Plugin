package com.hezhihu.plugin.dependencies

import com.hezhihu.gradle.plugin.base.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.BasePluginConvention
import org.json.JSONObject
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

        project.afterEvaluate { rootProject ->
            project.extensions.getByType(DepConfig::class.java).dependencies.apply {
                if(exists()){
                    appFrameworkFromFile(this).apply {
                        app.framework.forEach { framework ->
                            val frameworkId = framework.id
                            val frameworkProject = rootProject.getProject(":$frameworkId")
                            addDependencies2Project(frameworkProject,framework.dependencies,maven)
                            framework.modules.forEach { module ->
                                val moduleId = module.id
                                val moduleProject = rootProject.getProject(":$frameworkId:$moduleId")
                                addDependencies2Project(moduleProject,module.dependencies,maven)
                            }
                        }
                    }
                }
            }

//            project.subprojects.forEach{ sub ->
//                sub.afterEvaluate{
//                    appendModule2Host(it,dependenciesMap)
//                    dependenciesAAR2Code(it,dependenciesMap)
//                }
//                applyVersionAndGroup(project,dependenciesMap)
//            }
        }
    }

    /**
     * 添加依赖到Project
     */
    private fun addDependencies2Project(project: Project?,dependencies: Dependencies?,maven: JSONObject){
        project?.afterEvaluate {
            println("module：${project.name} 添加依赖")
            val moduleDependencies = dependencies?.dependencies()
            moduleDependencies?.keys?.forEach { dependenciesType ->
                moduleDependencies[dependenciesType]?.forEach{ mavenId ->
                    val mavenDependencies = maven.optString(mavenId)
                    project.dependencies.add(dependenciesType,mavenDependencies)
                    println("   ++++++ $dependenciesType $mavenDependencies}")
                }
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

    private fun List<Framework>.toMap(): HashMap<String,Dependencies?>{
        val map = hashMapOf<String,Dependencies?>()
        forEach {
            val groupId = it.id
            map[":$groupId"] = it.dependencies
            it.modules.forEach { module ->
                val moduleId = module.id
                map[":$groupId:$moduleId"] = module.dependencies
            }
        }
        return map
    }
}