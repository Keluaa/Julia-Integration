package com.github.keluaa.juliaintegration.embed

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.impl.JuliaLoader

class JuliaSession(path: String? = null) { // TODO: use

    private val INSTANCE: JuliaSession

    val jl: Julia

    init {
        if (path != null) {
            System.setProperty("juinko.julia_path", path)
        }
        jl = JuliaLoader.get()
        initProject()
        INSTANCE = this
    }

    fun initProject() {
        // TODO: create a project (where?), activate it, instantiate it (dl required packages)
    }
}