package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juliaintegration.JuliaLanguage
import com.intellij.psi.tree.IElementType

/**
 * Represents a `JuliaSyntax.Kind` or a range of kinds.
 *
 * There exists a hard limit of ~15000 of [IElementType] that can be registered.
 * JuliaSyntax.jl defines ~1000 of token kinds: this plugin shouldn't take up 1/15 of all possible types!
 * Therefore, each some token kind categories are represented as a single [JuliaKind].
 */
class JuliaKind(kindRange: Pair<Short, Short>, val name: String): IElementType(name, JuliaLanguage) {

    companion object {
        private val kindScript by lazy { KindScript.get(JuliaLoader.get()) }

        fun getKindFromName(name: String) = kindScript.kindFromName(name)

        /**
         * Given the `name` of a range (delimited by `BEGIN_$name` and `END_$name` in
         * `JuliaSyntax.jl:kinds.jl`), return the indices of the first and last kind in the range.
         */
        fun getKindRangeFromName(name: String) = kindScript.kindRangeFromName(name)

        fun getKindCount() = kindScript.kindCount()

        fun getErrorString(errorKind: JuliaKind) = kindScript.errorString(errorKind.kind)

        private fun duplicatePair(v: Short) = Pair(v, v)
    }

    constructor(kind: Short, name: String) : this(duplicatePair(kind), name)
    constructor(name: String) : this(getKindFromName(name), name)
    constructor(name: String, isRange: Boolean) : this(
        if (isRange) getKindRangeFromName(name)
        else duplicatePair(getKindFromName(name)),
        name
    )

    val kind = kindRange.first
    val lastKind = kindRange.second
    val isRange = kindRange.first != kindRange.second
    val range = kind..lastKind
    val rangeLength = lastKind - kind + 1
}
