package com.github.keluaa.juliaintegration.parser.psi

import com.github.keluaa.juliaintegration.lexer.JuliaKinds
import com.github.keluaa.juliaintegration.lexer.KindSets
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

object ElementFactory {

    fun createElement(node: ASTNode): PsiElement {
        // TODO: reorder
        return when (node.elementType) {
            //JuliaKinds.Whitespace(), JuliaKinds.NewlineWs() -> JuliaWhitespaceNode(node)
            JuliaKinds.Identifier(), JuliaKinds.MACRO_NAME() /* TODO: macros can be called with a different name (e.g. 'e"..." => macro e_str' */ -> JuliaIdentifierNode(node)
            //JuliaKinds.Semicolon() -> JuliaSemicolonNode(node)
            JuliaKinds.ASSIGNMENT() -> JuliaAssignmentNode(node)
            //JuliaKinds.DELIMITER() -> JuliaDelimiterNode(node)
            JuliaKinds.call() -> JuliaCallNode(node)
            JuliaKinds.macrocall() -> JuliaMacroCallNode(node)
            JuliaKinds.toplevel() -> JuliaTopLevelNode(node)
            JuliaKinds.string() -> JuliaStringNode(node)
            //in KindSets.KEYWORDS, in KindSets.CONTEXTUAL_KEYWORDS -> JuliaKeywordNode(node)
            in KindSets.COMMENTS -> JuliaCommentNode(node)
            in KindSets.LITERALS -> JuliaLiteralNode(node)
            in KindSets.OPERATORS -> JuliaOperatorNode(node)
            else -> ASTWrapperPsiElement(node)
            // TODO: return ASTWrapperPsiElement by default?
            //else -> throw AssertionError("Unknown element type: ${node.elementType}")
        }
    }

}