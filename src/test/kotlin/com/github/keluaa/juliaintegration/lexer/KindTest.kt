package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juliaintegration.lexer.KindSets.ALL_KEYWORDS
import com.github.keluaa.juliaintegration.lexer.KindSets.CONTEXTUAL_KEYWORDS
import com.github.keluaa.juliaintegration.lexer.KindSets.LITERALS
import com.github.keluaa.juliaintegration.lexer.KindSets.OPERATORS
import com.github.keluaa.juliaintegration.lexer.KindSets.PARSER
import com.github.keluaa.juliaintegration.lexer.KindSets.SYNTAX
import com.intellij.psi.tree.TokenSet
import org.junit.Assert
import org.junit.Test

class KindTest {

    @Test
    fun kindCount() {
        Assert.assertEquals(JuliaKind.getKindCount(), JuliaKinds.kinds.size)
    }

    private fun checkRange(name: String, firstKind: JuliaKind, lastKind: JuliaKind) {
        val (firstIdx, lastIdx) = JuliaKind.getKindRangeFromName(name)
        Assert.assertEquals("first-$name", firstIdx, firstKind.kind)
        Assert.assertEquals("last-$name", lastIdx, lastKind.lastKind)
    }

    private fun checkSet(name: String, set: TokenSet) {
        val tokens = set.types
        val firstTk = tokens.first() as JuliaKind
        val lastTk = tokens.last() as JuliaKind
        checkRange(name, firstTk, lastTk)
    }

    @Test
    fun kindSets() {
        checkSet("KEYWORDS", ALL_KEYWORDS)
        checkSet("CONTEXTUAL_KEYWORDS", CONTEXTUAL_KEYWORDS)
        checkSet("LITERAL", LITERALS)
        checkSet("OPS", OPERATORS)
        checkSet("PARSER_TOKENS", PARSER)
        checkSet("SYNTAX_KINDS", SYNTAX)
    }

    @Test
    fun unknownKind() {
        val kind = "ThisKindDoesNotExist"
        try {
            JuliaKind.getKindFromName(kind)
            Assert.fail("Expected UnknownKindException")
        } catch (e: UnknownKindException) {
            Assert.assertEquals("Unknown kind: $kind", e.message)
        }

        try {
            JuliaKind.getKindRangeFromName(kind)
            Assert.fail("Expected UnknownKindException")
        } catch (e: UnknownKindException) {
            Assert.assertEquals("Unknown kind range: BEGIN_$kind", e.message)
        }
    }

    @Test
    fun errorKind() {
        val errorKind = JuliaKinds.ErrorInvalidOperator.kind
        var errorStr = JuliaKind.getErrorString(errorKind)
        Assert.assertEquals("invalid operator", errorStr)

        val notErrorKind = JuliaKinds.PLUS.kind
        errorStr = JuliaKind.getErrorString(notErrorKind)
        Assert.assertEquals("", errorStr)
    }
}