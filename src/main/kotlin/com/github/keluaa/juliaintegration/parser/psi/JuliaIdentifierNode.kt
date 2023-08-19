package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

class JuliaIdentifierNode(node: ASTNode): ASTWrapperPsiElement(node), PsiNameIdentifierOwner {
    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }

    override fun getNameIdentifier(): PsiElement? {
        TODO("Not yet implemented")
    }
}