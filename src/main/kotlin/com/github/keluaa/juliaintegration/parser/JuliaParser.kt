package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.GCStack
import com.github.keluaa.juinko.Julia
import com.github.keluaa.juliaintegration.lexer.JuliaKinds
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.Stack
import com.sun.jna.Callback
import com.sun.jna.CallbackReference

/**
 * Julia AST parser. Relies on JuliaSyntax.jl for parsing.
 *
 * The AST is built from the Julia side by recursively parsing the tree made by `JuliaSyntax.build_tree`, calling the
 * appropriate JVM callback for each node. See 'resources/julia/parser.jl'.
 *
 * The Julia tree is made of `JuliaSyntax.GreenNode`, which has some very nice properties:
 *  - leaf nodes cover a single token
 *  - non-leaf nodes do not cover more tokens than what their children do
 *  - all tokens are part of the tree
 *
 * This makes the implementation very easy, bypassing the UTF-16 (JVM) / UTF-8 (Julia) encoding problem, since at no
 * point character positions are involved: we only advance the lexer one token at a time. And because
 * [com.github.keluaa.juliaintegration.lexer.JuliaLexer] relies on the same lexer as JuliaSyntax.jl, everything is
 * synchronized.
 */
class JuliaParser(private val jl: Julia): PsiParser {

    private val script = ParserScript.get(jl)

    class JuliaNodeInfo(private val nodeType: IElementType, private val marker: PsiBuilder.Marker) {
        fun markAsDone() = marker.done(nodeType)
    }

    private val nodeStack = Stack<JuliaNodeInfo>(20)  // Codes with an AST deeper than 20 are rare TODO: check again

    private lateinit var builder: PsiBuilder

    private val leafNodeCb = object : Callback {
        @Suppress("unused")
        fun invoke(kind: Short) {
            val nodeType = JuliaKinds.fromIdx(kind)
            val mark = builder.mark()
            builder.advanceLexer()
            mark.done(nodeType)
        }
    }

    private val enterNodeCb = object : Callback {
        @Suppress("unused")
        fun invoke(kind: Short) {
            val nodeType = JuliaKinds.fromIdx(kind)
            val mark = builder.mark()
            nodeStack.push(JuliaNodeInfo(nodeType, mark))
        }
    }

    private val leaveNodeCb = object : Callback {
        @Suppress("unused")
        fun invoke() {
            val nodeInfo = nodeStack.pop()
            nodeInfo.markAsDone()
        }
    }

    private val leafNodeCbPtr  = CallbackReference.getFunctionPointer(leafNodeCb)
    private val enterNodeCbPtr = CallbackReference.getFunctionPointer(enterNodeCb)
    private val leaveNodeCbPtr = CallbackReference.getFunctionPointer(leaveNodeCb)

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        this.builder = builder
        // builder.setDebugMode(true)

        val rootMarker = builder.mark()  // Implicitly skips leading whitespaces and comments, moving the current offset

        val origText = builder.originalText
        val text = origText.subSequence(builder.currentOffset, origText.length).toString()

        runParser(text)
        rootMarker.done(root)

        return builder.treeBuilt
    }

    private fun runParser(text: String) {
        jl.runInJuliaThread {
            GCStack(jl, 1).use { stack ->
                val version = null  // TODO: set version from project config
                val parser = script.createParser(text, leafNodeCbPtr, enterNodeCbPtr, leaveNodeCbPtr, version)
                stack[0] = parser

                while (!builder.eof()) {
                    script.runParser(parser)  // Each call parses a complete statement
                }
            }
        }
    }
}