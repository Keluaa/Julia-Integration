package com.github.keluaa.juliaintegration

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object JuliaFileType : LanguageFileType(JuliaLanguage) {
    override fun getName(): String {
        return "Julia File"
    }

    override fun getDescription(): String {
        return "Julia script file"
    }

    override fun getDefaultExtension(): String {
        return "jl"
    }

    override fun getIcon(): Icon {
        return JuliaIcons.Dots
    }
}