package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_value_t
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.sun.jna.Pointer

class JuliaParser(private val jl: Julia): PsiParser {

    private var parser: jl_value_t = Pointer(0)
    private var textOffset = 0

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        builder.setDebugMode(true)  // TODO: remove
        val rootMarker = builder.mark()
        val text = builder.originalText.toString()
        textOffset = builder.currentOffset
        jl.runInJuliaThread {
            startParser(text) // TODO: check if there is any need to strip 'text' to only keep the part which needs to be parsed
            parseRange(builder)
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun startParser(text: String) {
        jl.runInJuliaThread {
            deleteParser()  // Delete any previous parser object


        }
    }

    private fun parseRange(builder: PsiBuilder) {

    }

    private fun deleteParser() {
        if (Pointer.nativeValue(parser) != 0L) {
            jl.runInJuliaThread {
                jl.memory.remove(parser)
            }
        }
    }

    fun finalize() = deleteParser()
}