package com.github.keluaa.juliaintegration.lexer

import com.sun.jna.Structure

/**
 * JNA mirror of the `LexerState` struct defined in "resources/julia/lexer.jl"
 */
@Structure.FieldOrder("startByte", "endByte", "tokenKind")
class LexerState: Structure() {

    init {
        autoWrite = false
        autoRead = false
    }

    /**
     * Offset to the first character of the token (0-indexed)
     */
    @JvmField
    var startByte: Long = 0

    /**
     * Offset to the character after the token (0-indexed)
     */
    @JvmField
    var endByte: Long = 0

    /**
     * Index in the `Token.Kind` enum of Tokenize.jl
     */
    @JvmField
    var tokenKind: Short = 0
}