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

        project.run {
            project.rootProject.file("dependencies.json").apply {
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
                                addDependencies2Project(moduleProject,framework.allDependencies,maven)
                            }
                        }

                        ///maven 依赖转换为源码依赖
                        val modules = app.framework.toModules();
                        project.subprojects.forEach{ sub ->
                            sub.afterEvaluate{
                                dependenciesAAR2Code(it,modules)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加依赖到Project
     */
    private fun addDependencies2Project(project: Project?,dependencies: Dependencies?,maven: JSONObject){
        project?.afterEvaluate {
            val moduleDependencies = dependencies?.dependencies()
            moduleDependencies?.apply {
                val log = LogUtil("添加依赖 {","}")
                keys.forEach { dependenciesType ->
                    moduleDependencies[dependenciesType]?.forEach{ mavenId ->
                        val mavenDependencies = maven.optString(mavenId)
                        project.dependencies.add(dependenciesType,mavenDependencies)
                        log.appendln("   ++++++ $dependenciesType $mavenDependencies")
                    }
                }
                log.print()
            }
        }
    }

    private fun dependenciesAAR2Code(project: Project,dependenciesMap: Modules){
        project.afterEvaluate{
            val log = LogUtil("替换模块 {","}")
            it.configurations.forEach{ configuration ->
                val depends = configuration.dependencies
                val size = depends.size - 1
                for(index in size downTo  0){
                    val dependency = depends.elementAt(index)
                    val dependencyId = dependency.name
                    dependency.group?.also { dependencyGroup ->
                        dependenciesMap.get(dependencyGroup)?.run {
                            get(dependencyId)?.apply {
                                log.append("   $dependencyGroup:$dependencyId ⥂ ")
                                it.getProject(id)?.run {
                                    depends.add(it.dependencies.create(this))
                                    depends.remove(dependency)
                                    log.appendln(this.toString())
                                }
                            }
                        }
                    }
                }
            }
            log.print()
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


    /**
     * 依赖转换为代码模块模型
     */
    private fun List<Framework>.toModules(): Modules{
        val modules = Modules()
        forEach {
            modules.addGroup(Modules.Group(it.id,it.group,it.path,it.version).apply {
                it.modules.forEach { module ->
                    addModule(Modules.Module(module.id,module.path))
                }
            })
        }
        return modules
    }
}