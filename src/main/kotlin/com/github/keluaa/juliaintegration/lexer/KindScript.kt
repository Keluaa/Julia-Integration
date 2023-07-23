package com.github.keluaa.juliaintegration.lexer

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.jl_value_t
import com.github.keluaa.juliaintegration.embed.JuliaScript
import com.sun.jna.Function

class KindScript private constructor(jl: Julia, module: jl_value_t) : JuliaScript(jl, module) {

    companion object {
        fun get(jl: Julia) = get<KindScript>(jl, "/julia/kinds.jl")
    }

    private val FPTR_kind_count = jl.jl_unbox_voidpointer(getGlobal("FPTR_kind_count"))
    private val FPTR_kind_from_name = jl.jl_unbox_voidpointer(getGlobal("FPTR_kind_from_name"))
    private val FPTR_kind_range_from_name = jl.jl_unbox_voidpointer(getGlobal("FPTR_kind_range_from_name"))
    private val FPTR_error_string = jl.jl_unbox_voidpointer(getGlobal("FPTR_error_string"))

    private val FUNC_kind_count = Function.getFunction(FPTR_kind_count)
    private val FUNC_kind_from_name = Function.getFunction(FPTR_kind_from_name, Function.C_CONVENTION, "utf-8")
    private val FUNC_kind_range_from_name = Function.getFunction(FPTR_kind_range_from_name, Function.C_CONVENTION, "utf-8")
    private val FUNC_error_string = Function.getFunction(FPTR_error_string, Function.C_CONVENTION, "utf-8")

    fun kindCount() = FUNC_kind_count.invokeLong(emptyArray()).toInt()

    fun kindFromName(name: String): Short {
        val kindValue = FUNC_kind_from_name.invokeInt(arrayOf(name)).toShort()
        if (kindValue == (-1).toShort()) throw UnknownKindException(name)
        return kindValue
    }

    fun kindRangeFromName(range: String): Pair<Short, Short> {
        // The Tuple{Int16, Int16} is packed as an Int32
        val tuple = FUNC_kind_range_from_name.invokeInt(arrayOf(range)).toUInt()
        val first = (tuple and 0x0000_FFFFu shr  0).toShort()
        val last  = (tuple and 0xFFFF_0000u shr 16).toShort()
        if (first == (-1).toShort()) throw UnknownKindException(range, true)
        return Pair(first, last)
    }

    fun errorString(kind: Short) = FUNC_error_string.invokeString(arrayOf(kind), false)!!
}