package com.github.keluaa.juliaintegration.parser.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

/**
 * A (possibly) interpolated string, composed of string literals [JuliaLiteralNode].
 *
 * Assigned to a `K"string"` node.
 */
class JuliaStringNode(node:ASTNode): ASTWrapperPsiElement(node)
