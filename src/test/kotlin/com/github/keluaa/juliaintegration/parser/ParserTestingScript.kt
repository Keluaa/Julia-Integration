package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.GCStack
import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_value_t
import com.github.keluaa.juliaintegration.embed.JuliaScript
import com.sun.jna.Structure

class ParserTestingScript private constructor(jl: Julia, module: jl_value_t) : JuliaScript(jl, module) {

    companion object {
        fun get(jl: Julia) = get<ParserTestingScript>(jl, "/julia/parser_testing.jl")
    }

    private val FUNC_parse_all_and_build_tree = jl.getModuleObj(module, "parse_all_and_build_tree")

    internal fun parseAllAndBuildTree(text: String): Array<Structure> {
        GCStack(jl, 3).use { stack ->
            stack[0] = jl.jl_pchar_to_string(text, text.length.toLong())  // Implicit UTF-16 to UTF-8 conversion
            stack[1] = jl.jl_nothing()  // Always use the current Julia version

            val tree = jl.jl_call(FUNC_parse_all_and_build_tree, stack.array(), 2)
            jl.exceptionCheck()
            stack[2] = tree!!

            val boxedTreePtr = jl.jl_call1(jl.getBaseObj("pointer"), tree)
            jl.exceptionCheck()
            val treePtr = jl.jl_unbox_voidpointer(boxedTreePtr!!)

            val treeSize = jl.jl_array_size(tree, 0)

            val refTreeBase = Structure.newInstance(ParserTest.ReferenceNode::class.java, treePtr)
            return refTreeBase.toArray(treeSize.toInt())
        }
    }
}