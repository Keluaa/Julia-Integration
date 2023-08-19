package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juliaintegration.JuliaLanguage
import com.github.keluaa.juliaintegration.embed.JuliaSession
import com.github.keluaa.juliaintegration.lexer.JuliaLexer
import com.github.keluaa.juliaintegration.lexer.KindSets
import com.github.keluaa.juliaintegration.parser.psi.ElementFactory
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType

class JuliaParserDefinition : ParserDefinition {

    object Util {
        @JvmStatic
        val FILE = IFileElementType(JuliaLanguage)
    }

    private val juliaSession: JuliaSession
    private val jl: Julia

    init {
        juliaSession = JuliaSession.get()  // TODO: move this to a separate service
        jl = juliaSession.jl
    }

    override fun createLexer(project: Project?) = JuliaLexer(jl)
    override fun createParser(project: Project?) = JuliaParser(jl)

    override fun getFileNodeType() = Util.FILE

    override fun getCommentTokens() = KindSets.COMMENTS
    override fun getStringLiteralElements() = KindSets.STRINGS

    override fun createElement(node: ASTNode?): PsiElement {
        if (node == null) throw AssertionError("null node")
        return ElementFactory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider) = JuliaFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        // TODO
        return ParserDefinition.SpaceRequirements.MAY
    }
}