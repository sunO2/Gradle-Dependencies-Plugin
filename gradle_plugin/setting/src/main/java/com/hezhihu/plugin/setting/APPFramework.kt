package com.hezhihu.plugin.setting

data class APPFramework(
    val app: App
)

data class App(
    val framework: List<Framework>,
    val host: Host

) {
    override fun toString(): String {
        return "App(framework=$framework, host=$host)"
    }
}

data class Framework(
    val dependencies: Dependencies,
    val groupId: String,
    val modules: List<Module>,
    val path: String,
    val version: String
) {
    override fun toString(): String {
        return "Framework(dependencies=$dependencies, groupId='$groupId', modules=$modules, path='$path', version='$version')"
    }
}

data class Host(
    val path: String

) {
    override fun toString(): String {
        return "Host(path='$path')"
    }
}

data class Dependencies(
    val api: List<String>,
    val implementation: List<String>

) {
    override fun toString(): String {
        return "Dependencies(api=$api, implementation=$implementation)"
    }

    /**
     * 依赖库
     */
    fun dependencies(): Map<String,List<String>>{
        return mapOf(
            "api" to api,
            "implementation" to implementation
        )
    }
}

data class Module(
    val dependencies: Dependencies,
    val id: String,
    val path: String

) {
    override fun toString(): String {
        return "Module(dependencies=$dependencies, id='$id', path='$path')"
    }
}
