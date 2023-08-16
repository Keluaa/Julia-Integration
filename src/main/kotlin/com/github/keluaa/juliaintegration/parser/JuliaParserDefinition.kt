package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juinko.types.JuliaOptions
import com.github.keluaa.juliaintegration.JuliaLanguage
import com.github.keluaa.juliaintegration.lexer.JuliaLexer
import com.github.keluaa.juliaintegration.lexer.KindSets
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class JuliaParserDefinition : ParserDefinition {

    companion object {
        @JvmStatic
        val FILE = IFileElementType(JuliaLanguage)
    }

    private val jl: Julia

    init {
        // TODO: move this in a separate service
        JuliaLoader.loadLibrary()
        val options = JuliaLoader.getOptions()
        options.setNumThreads(JuliaOptions.JL_OPTIONS_THREADS_AUTO)
        jl = JuliaLoader.get()
    }

    override fun createLexer(project: Project?) = JuliaLexer(jl)

    override fun createParser(project: Project?): PsiParser {
        TODO("Not yet implemented")
    }

    override fun getFileNodeType() = FILE

    override fun getCommentTokens() = KindSets.COMMENTS
    override fun getStringLiteralElements() = KindSets.STRINGS

    override fun createElement(node: ASTNode?): PsiElement {
        TODO("Not yet implemented")
    }

    override fun createFile(viewProvider: FileViewProvider) = JuliaFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        // TODO
        return ParserDefinition.SpaceRequirements.MAY
    }
}