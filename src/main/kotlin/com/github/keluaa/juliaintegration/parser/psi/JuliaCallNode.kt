package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.refactoring.changeSignature.ChangeInfo
import com.intellij.refactoring.changeSignature.PsiCallReference

open class JuliaCallNode(node: ASTNode): ASTWrapperPsiElement(node), PsiCallReference {
    override fun getElement(): PsiElement {
        TODO("Not yet implemented")
    }

    override fun getRangeInElement(): TextRange {
        TODO("Not yet implemented")
    }

    override fun resolve(): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun getCanonicalText(): String {
        TODO("Not yet implemented")
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        TODO("Not yet implemented")
    }

    override fun bindToElement(element: PsiElement): PsiElement {
        TODO("Not yet implemented")
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSoft(): Boolean {
        TODO("Not yet implemented")
    }

    override fun handleChangeSignature(changeInfo: ChangeInfo?): PsiElement {
        TODO("Not yet implemented")
    }

}


open class JuliaOperatorNode(node: ASTNode): JuliaCallNode(node)

open class JuliaMacroCallNode(node: ASTNode): JuliaCallNode(node)
