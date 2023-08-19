package com.github.keluaa.juliaintegration.embed

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.JuliaVersion
import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juinko.types.JuliaOptions

/**
 * Manages the Julia session (Julia library loading + project instantiation) with a singleton.
 */
class JuliaSession private constructor() {

    companion object {
        private lateinit var INSTANCE: JuliaSession

        var juliaPath: String? = null
            private set

        fun setJuliaPath(path: String) {
            if (::INSTANCE.isInitialized)
                throw Exception("Cannot set Julia path since the session is already initialized")
            juliaPath = path
        }

        fun get(): JuliaSession {
            if (!::INSTANCE.isInitialized)
                INSTANCE = JuliaSession()
            return INSTANCE
        }

        fun getJl(): Julia = get().jl
    }

    val jl: Julia

    init {
        if (juliaPath != null) {
            System.setProperty("juinko.julia_path", juliaPath!!)
        }

        JuliaLoader.loadLibrary()
        // TODO: set juliaPath to the actual path to the loaded library for convenience

        if (JuliaVersion < JuliaVersion(1, 9)) {
            throw Exception("The Julia-Integration plugin only supports Julia 1.9 and above, got: ${JuliaVersion.get()}")
        }

        val options = JuliaLoader.getOptions()
        options.setNumThreads(JuliaOptions.JL_OPTIONS_THREADS_AUTO)

        jl = JuliaLoader.get()

        initProject()
    }

    private fun initProject() {
        // TODO: create a project (where?), activate it, instantiate it (dl required packages)
    }
}