package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.GCStack
import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.JuliaVersion
import com.github.keluaa.juinko.jl_value_t
import com.github.keluaa.juliaintegration.embed.JuliaScript
import com.sun.jna.*

class ParserScript private constructor(jl: Julia, module: jl_value_t) : JuliaScript(jl, module) {

    companion object {
        fun get(jl: Julia) = get<ParserScript>(jl, "/julia/parser.jl")
    }

    private val FUNC_create_parser = jl.getModuleObj(module, "create_parser")
    private val FUNC_run_parser = jl.getModuleObj(module, "run_parser")

    fun createParser(text: String,
                     leafCb: Pointer, enterCb: Pointer, leaveCb: Pointer,
                     version: JuliaVersion?): jl_value_t {
        GCStack(jl, 5).use { stack ->
            stack[0] = jl.jl_pchar_to_string(text, text.length.toLong())  // Implicit UTF-16 to UTF-8 conversion
            stack[1] = jl.jl_box_voidpointer(leafCb)
            stack[2] = jl.jl_box_voidpointer(enterCb)
            stack[3] = jl.jl_box_voidpointer(leaveCb)
            stack[4] = if (version == null)
                jl.jl_nothing()  // Default to the current Julia version
            else {
                val versionString = version.toString()
                jl.jl_pchar_to_string(versionString, versionString.toLong())
            }

            val parser = jl.jl_call(FUNC_create_parser, stack.array(), stack.size)
            jl.exceptionCheck()
            return parser!!
        }
    }

    fun runParser(parser: jl_value_t) {
        jl.jl_call1(FUNC_run_parser, parser)
        jl.exceptionCheck()
    }
}