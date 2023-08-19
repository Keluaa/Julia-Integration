package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiLiteralValue

class JuliaLiteralNode(node: ASTNode): ASTWrapperPsiElement(node), PsiLiteralValue {
    override fun getValue(): Any? {
        TODO("Not yet implemented")
    }
}