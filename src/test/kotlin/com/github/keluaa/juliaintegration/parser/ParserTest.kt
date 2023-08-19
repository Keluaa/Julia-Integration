package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juliaintegration.lexer.JuliaKinds
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.testFramework.ParsingTestCase
import com.sun.jna.Structure
import com.sun.jna.Structure.FieldOrder
import org.junit.Assert

class ParserTest: ParsingTestCase("", "jl", true, JuliaParserDefinition()) {

    override fun getTestDataPath() = "src/test/resources/juliaSamples"

    override fun skipSpaces() = false
    override fun includeRanges() = true

    private val jl = JuliaLoader.get()

    @FieldOrder("kind", "startOffset", "endOffset")
    internal class ReferenceNode : Structure() {
        @JvmField var kind: Short = 0
        @JvmField var startOffset: Long = 0
        @JvmField var endOffset: Long = 0
    }

    private fun checkNode(refNode: ReferenceNode, index: Int, element: PsiElement) {
        val expectedType = JuliaKinds.fromIdx(refNode.kind)
        val msg = "At node $index of type $expectedType"
        Assert.assertEquals(msg, expectedType, element.elementType)
        Assert.assertEquals(msg, refNode.startOffset, element.textRange.startOffset)
        Assert.assertEquals(msg, refNode.endOffset, element.textRange.endOffset)
    }

    private fun compareWithReferenceTree(refNodes: Array<Structure>, parentRefIndex: Int, element: PsiElement): Int {
        var refIndex = parentRefIndex
        checkNode(refNodes[refIndex] as ReferenceNode, refIndex, element)
        refIndex += 1
        for (child in element.children) {
            refIndex = compareWithReferenceTree(refNodes, refIndex, element)
        }
        return refIndex
    }

    private fun printRefTree(refNodes: Array<Structure>) {
        for (i in refNodes.indices) {
            val refNode = refNodes[i] as ReferenceNode
            val kind = JuliaKinds.fromIdx(refNode.kind)
            println("[$i] (${refNode.startOffset}:${refNode.endOffset}) ${kind.debugName}")
        }
    }

    private fun compareWithReference(rootNode: PsiElement) {
        // The reference tree is flattened as an array to make it much easier to transfer from Julia to the JVM
        jl.runInJuliaThread {
            val refTree = ParserTestingScript.get(jl).parseAllAndBuildTree(myFile.text)
            printRefTree(refTree)
            println(toParseTreeText(myFile, skipSpaces(), includeRanges()))
            compareWithReferenceTree(refTree, 0, rootNode)
        }
    }

    fun testSimple() {
        doTest(false)
        //println(toParseTreeText(myFile, skipSpaces(), includeRanges()))
        //PsiTreeVisitor().visitFile(myFile)
        compareWithReference(myFile)
    }

    fun testUnicode() {
        doTest(false)
        compareWithReference(myFile)
        println(toParseTreeText(myFile, skipSpaces(), includeRanges()))
    }
}