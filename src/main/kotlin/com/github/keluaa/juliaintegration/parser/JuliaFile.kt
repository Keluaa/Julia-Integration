package com.github.keluaa.juliaintegration.parser

import com.github.keluaa.juliaintegration.JuliaFileType
import com.github.keluaa.juliaintegration.JuliaLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class JuliaFile(viewProvider: FileViewProvider): PsiFileBase(viewProvider, JuliaLanguage) {
    override fun getFileType() = JuliaFileType
    override fun toString() = "Julia File"
}