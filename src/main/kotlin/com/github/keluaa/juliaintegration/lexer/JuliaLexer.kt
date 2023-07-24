package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_value_t
import com.intellij.lexer.LexerBase
import com.intellij.openapi.util.text.Strings
import com.intellij.psi.tree.IElementType
import com.sun.jna.Pointer

class JuliaLexer(private val jl: Julia): LexerBase() {
    // TODO : ideally, doc strings should use a Markdown parsing.lexer, and Julia code inside those
    //  doc strings should use a Julia parsing.lexer
    //  See https://plugins.jetbrains.com/docs/intellij/implementing-lexer.html#embedded-language

    private val script = LexerScript.get(jl)

    private var buffer: CharSequence = Strings.EMPTY_CHAR_SEQUENCE
    private var bufferEndOffset: Int = 0

    private val lexer: jl_value_t = Pointer(0)
    private val state: LexerState = LexerState()
    var isDone = false
        private set

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        jl.runInJuliaThread {
            deleteLexer()

            val text = buffer.subSequence(startOffset, endOffset).toString()
            this.buffer = buffer
            bufferEndOffset = endOffset

            val lexer = script.createLexer(text)
            Pointer.nativeValue(this.lexer, Pointer.nativeValue(lexer))
            jl.memory.insert(lexer)

            isDone = script.startLexer(lexer, state)
        }
    }

    override fun advance() {
        // Assume that we are still in the same thread that called 'start'
        if (isDone) return
        isDone = script.advanceLexer(lexer, state)
    }

    override fun getTokenType(): IElementType? {
        return if (isDone) null else JuliaKinds.fromIdx(state.tokenKind)
    }

    override fun getState() = 0
    override fun getTokenStart() = state.startByte.toInt()
    override fun getTokenEnd() = state.endByte.toInt()
    override fun getBufferSequence() = buffer
    override fun getBufferEnd() = bufferEndOffset

    private fun deleteLexer() {
        if (Pointer.nativeValue(lexer) != 0L) {
            jl.runInJuliaThread {
                jl.memory.remove(lexer)
            }
        }
    }

    fun finalize() = deleteLexer()
}