package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_module_t
import com.github.keluaa.juinko.jl_value_t
import com.github.keluaa.juliaintegration.embed.JuliaScript

class LexerScript private constructor(jl: Julia, module: jl_module_t): JuliaScript(jl, module) {

    companion object {
        fun get(jl: Julia) = get<LexerScript>(jl, "/julia/lexer.jl")
    }

    private val FUNC_create_lexer = jl.getModuleObj(module, "create_lexer")
    private val FUNC_start_lexer = jl.getModuleObj(module, "start_lexer")
    private val FUNC_advance_lexer = jl.getModuleObj(module, "advance_lexer")

    fun createLexer(text: String): jl_value_t {
        val boxedText = jl.jl_pchar_to_string(text, text.length.toLong())
        val lexer = jl.jl_call1(FUNC_create_lexer, boxedText)
        jl.exceptionCheck()
        return lexer!!
    }

    fun startLexer(lexer: jl_value_t, state: LexerState): Boolean {
        val boxedState = jl.jl_box_voidpointer(state.pointer)
        val boxedDone = jl.jl_call2(FUNC_start_lexer, lexer, boxedState)
        jl.exceptionCheck()
        state.read()
        return boxedDone == jl.jl_true()
    }

    fun advanceLexer(lexer: jl_value_t, state: LexerState): Boolean {
        val boxedState = jl.jl_box_voidpointer(state.pointer)
        val boxedDone = jl.jl_call2(FUNC_advance_lexer, lexer, boxedState)
        jl.exceptionCheck()
        state.read()
        return boxedDone == jl.jl_true()
    }
}