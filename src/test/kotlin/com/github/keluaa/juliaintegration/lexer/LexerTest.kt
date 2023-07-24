package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juinko.jl_module_t
import com.github.keluaa.juliaintegration.embed.JuliaScript
import com.intellij.psi.tree.IElementType
import groovy.lang.Tuple3
import org.codehaus.plexus.util.StringUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URL
import kotlin.math.round
import kotlin.system.measureTimeMillis

class LexerTest {

    class UntokenizeScript private constructor(jl: Julia, module: jl_module_t): JuliaScript(jl, module) {
        companion object {
            fun get(jl: Julia) = get<UntokenizeScript>(jl, "/julia/untokenize.jl")
        }

        private val FUNC_token_strings = jl.getModuleObj(module, "token_strings")

        fun tokenStrings(text: String): Array<String> {
            val boxedText = jl.jl_pchar_to_string(text, text.length.toLong())
            val boxedStrings = jl.jl_call1(FUNC_token_strings, boxedText)
            jl.exceptionCheck()
            boxedStrings!!  // Vector{String}
            val count = jl.jl_array_len(boxedStrings).toInt()
            val strings = Array(count) { i ->
                val tokenString = jl.jl_arrayref(boxedStrings, i.toLong())
                jl.jl_string_ptr(tokenString)
            }
            jl.exceptionCheck()
            return strings
        }
    }


    private val jl = JuliaLoader.get()
    private lateinit var lexer: JuliaLexer

    @Before
    fun setUp() {
        lexer = JuliaLexer(jl)
    }

    @After
    fun tearDown() {
        lexer.finalize()
    }

    private fun sampleFile(name: String): URL {
        return LexerTest::class.java.getResource("/juliaSamples/$name")!!
    }

    private fun lexString(text: String, start: Int = 0, end: Int = text.length): List<Tuple3<IElementType, Int, Int>> {
        lexer.start(text, start, end)

        val tokens = ArrayDeque<Tuple3<IElementType, Int, Int>>()
        do {
            tokens.add(Tuple3(lexer.tokenType, lexer.tokenStart, lexer.tokenEnd))
            lexer.advance()
        } while (!lexer.isDone)

        return tokens
    }

    private fun tokenString(text: String, token: Tuple3<IElementType, Int, Int>) = tokenString(text, token.v2, token.v3)
    private fun tokenString(text: String, start: Int, end: Int) = text.substring(start, end)

    private fun printToken(text: String, token: Tuple3<IElementType, Int, Int>): String = printToken(text, token.v1, token.v2, token.v3)
    private fun printToken(text: String, token: IElementType, start: Int, end: Int): String {
        return String.format("%-20s [%3d - %3d]: '%s'",
            token.toString(), start, end, StringUtils.escape(text.substring(start, end)))
    }

    @Test
    fun lexSimpleFile() {
        val text = sampleFile("simple.jl").readText()
        val tokens = lexString(text)
        val expectedStrings = UntokenizeScript.get(jl).tokenStrings(text)
        for ((token, expectedString) in tokens.zip(expectedStrings)) {
            Assert.assertEquals(expectedString, tokenString(text, token))
//            println("${printToken(text, token)} ('${StringUtils.escape(expectedString)}')")
        }
    }

    @Test
    fun lexUnicodeFile() {
        val text = sampleFile("unicode.jl").readText()
        val tokens = lexString(text)
        val expectedStrings = UntokenizeScript.get(jl).tokenStrings(text)
        for ((token, expectedString) in tokens.zip(expectedStrings)) {
            Assert.assertEquals(expectedString, tokenString(text, token))
//            println("${printToken(text, token)} ('${StringUtils.escape(expectedString)}')")
        }
    }

    @Test
    fun profileBigFile() {
        val text = URL("https://raw.githubusercontent.com/JuliaLang/julia/master/test/core.jl").readText()
        lexString(text, 0, 500)  // Warmup

        var tokenCount: Int
        val timeMs = measureTimeMillis {
            val tokens = lexString(text)
            tokenCount = tokens.size
        }

        val timeSec = timeMs / 1e3
        val tokSpeed = round(tokenCount / timeSec).toLong()
        val charSpeed = round(text.length / timeSec).toLong()
        println("$timeMs ms to lex ${text.length} chars into $tokenCount tokens: $tokSpeed tok/s, $charSpeed char/s")
    }
}