package com.github.keluaa.juliaintegration.embed

import com.github.keluaa.juinko.GCStack
import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_module_t
import com.github.keluaa.juinko.jl_value_t
import com.jetbrains.rd.util.getOrCreate
import com.sun.jna.Callback
import com.sun.jna.CallbackReference
import com.sun.jna.Pointer
import java.util.MissingResourceException
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.isAccessible

/**
 * Wrapper around a Julia file stored in the resources. This file is evaluated once.
 *
 * Some additional variables are added into the Julia environment to allow to include other resource files from the
 * script.
 */
open class JuliaScript(val jl: Julia, val module: jl_value_t) {

    companion object {
        private val SCRIPTS = HashMap<String, JuliaScript>()
        private var isInitialized = false
        private var safeLoadFileString: jl_value_t = Pointer.createConstant(0)

        inline fun <reified T:JuliaScript> get(jl: Julia, path: String): T {
            val ctor = T::class.constructors.first()
            ctor.isAccessible = true
            return get(jl, path, ctor) as T
        }

        fun <T:JuliaScript> get(jl: Julia, path: String, ctor: KFunction<T>): JuliaScript {
            return synchronized(SCRIPTS) {
                SCRIPTS.getOrCreate(path) {
                    val scriptModule = create(it)
                    return@getOrCreate ctor.call(jl, scriptModule)
                }
            }
        }

        private fun create(path: String): jl_module_t {
            val contents = JuliaScript::class.java.getResource(path)?.readText()
                ?: throw MissingResourceException("Missing Julia script resource: '$path'", "JuliaScript", path)

            val jl = JuliaSession.getJl()

            var scriptModule: jl_value_t? = null
            jl.runInJuliaThread {
                init(jl)
                scriptModule = loadFileString(jl, contents, "resources$path", jl.jl_main_module())
            }
            return scriptModule!!
        }

        private fun loadFileString(jl: Julia, contents: String, filename: String, topModule: jl_module_t): jl_value_t {
            GCStack(jl, 5).use { stack ->
                val boxedContents = jl.jl_cstr_to_string(contents)
                stack[0] = boxedContents

                val boxedFilename = jl.jl_cstr_to_string(filename)
                stack[1] = boxedFilename

                // jl.jl_load_file_string has the disadvantage of throwing errors, which require a Julian try-catch.
                // To properly handle errors, the call must be made through a jl_call.
                val ret = jl.jl_call3(safeLoadFileString, boxedContents, boxedFilename, topModule)
                jl.exceptionCheck()
                return ret!!
            }
        }

        private val getResourceCallback = object : Callback {
            fun invoke(path: String) = JuliaScript::class.java.getResource(path)?.readText() ?: ""
        }

        private fun init(jl: Julia) {
            if (isInitialized) return

            val main = jl.jl_main_module()

            // const IS_EMBEDDED = true
            let {
                val varName = jl.jl_symbol("IS_EMBEDDED")
                val binding = jl.jl_get_binding_wr(main, varName, 1)
                jl.exceptionCheck()
                binding!!
                jl.jl_declare_constant(binding, main, varName)
                jl.jl_checked_assignment(binding, main, varName, jl.jl_true())
                jl.exceptionCheck()
            }

            // const GET_RESOURCE_FPTR = Ptr{Cvoid}(...)
            GCStack(jl, 3).use { stack ->
                val cbFptr = CallbackReference.getFunctionPointer(getResourceCallback)
                val boxedCbFptr = jl.jl_box_voidpointer(cbFptr)
                stack[0] = boxedCbFptr

                val varName = jl.jl_symbol("GET_RESOURCE_FPTR")
                val binding = jl.jl_get_binding_wr(main, varName, 1)
                jl.exceptionCheck()
                binding!!
                jl.jl_declare_constant(binding, main, varName)
                jl.jl_checked_assignment(binding, main, varName, boxedCbFptr)
                jl.exceptionCheck()
            }

            val initFile = getResourceCallback.invoke("/julia/init.jl")
            jl.jl_eval_string(initFile)

            safeLoadFileString = jl.getMainObj("safe_load_file_string")

            isInitialized = true
        }
    }

    fun getGlobal(name: String) = jl.getModuleObj(module, name)
}