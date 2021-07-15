package com.github.earthcomputer.jsmacrosindexing.services

import com.github.earthcomputer.jsmacrosindexing.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
