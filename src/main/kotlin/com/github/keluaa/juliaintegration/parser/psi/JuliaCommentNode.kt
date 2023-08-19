package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiComment

class JuliaCommentNode(node: ASTNode): ASTWrapperPsiElement(node), PsiComment {
    override fun getTokenType() = node.elementType
}