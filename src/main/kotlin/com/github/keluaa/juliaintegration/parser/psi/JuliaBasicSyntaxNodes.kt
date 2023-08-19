package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace


class JuliaKeywordNode(node: ASTNode): ASTWrapperPsiElement(node)

class JuliaAssignmentNode(node: ASTNode): ASTWrapperPsiElement(node)

class JuliaTopLevelNode(node: ASTNode): ASTWrapperPsiElement(node)

class JuliaWhitespaceNode(node: ASTNode): ASTWrapperPsiElement(node), PsiWhiteSpace

class JuliaSemicolonNode(node: ASTNode): ASTWrapperPsiElement(node)

class JuliaDelimiterNode(node: ASTNode): ASTWrapperPsiElement(node)
