package com.github.keluaa.juliaintegration.embed

import com.github.keluaa.juinko.Julia
import com.github.keluaa.juinko.JuliaException
import com.github.keluaa.juinko.impl.JuliaLoader
import com.github.keluaa.juinko.jl_value_t
import com.sun.jna.Function
import org.junit.Assert
import org.junit.Test

class ScriptingTest {

    companion object {
        val jl = JuliaLoader.get()
    }

    class TestScript private constructor(jl: Julia, module: jl_value_t) : JuliaScript(jl, module) {
        companion object {
            fun get(jl: Julia) = get<TestScript>(jl, "/julia/test.jl")
        }

        private val FPTR_some_function = jl.jl_unbox_voidpointer(getGlobal("FPTR_some_function"))
        private val FUNC_some_function = Function.getFunction(FPTR_some_function)

        val includeSuccess = jl.getModuleObj(module, "INCLUDE_SUCCESS")

        fun someFunction(i: Int): Int = FUNC_some_function.invokeInt(arrayOf(i))
    }

    class IncludeFailScript private constructor(jl: Julia, module: jl_value_t) : JuliaScript(jl, module) {
        companion object {
            fun get(jl: Julia) = get<IncludeFailScript>(jl, "/julia/include_fail.jl")
        }
    }

    @Test
    fun testScript() {
        val testScript = TestScript.get(jl)
        Assert.assertEquals(jl.jl_true(), testScript.includeSuccess)
    }

    @Test
    fun someFunction() {
        val testScript = TestScript.get(jl)
        val i = 42
        val res = testScript.someFunction(i)
        Assert.assertEquals(i + 1, res)
    }

    @Test
    fun cFunctionInJVMThread() {
        // `@cfunction`s should be safely callable from non-adopted threads
        val jlThreadCount = jl.jl_n_threads()
        val results = Array(jlThreadCount * 2) { -1 }
        val adopted = Array(jlThreadCount * 2) { false }
        val threads = Array(jlThreadCount * 2) { i ->
            Thread {
                val testScript = TestScript.get(jl)
                adopted[i] = jl.inJuliaThread()
                results[i] = testScript.someFunction(i)
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        jl.exceptionCheck()

        results.forEachIndexed { i, res ->
            Assert.assertEquals(i + 1, res)
        }

        val allAdopted = adopted.reduce { res, isAdopted -> res and isAdopted }
        Assert.assertFalse("All JVM used where already adopted", allAdopted)
    }

    @Test
    fun includeFailScript() {
        try {
            IncludeFailScript.get(jl)
            Assert.fail()
        } catch (e: JuliaException) {
            Assert.assertTrue(e.message, e.message.contains("/julia/file_that_doesnt_exist.jl"))
        }
    }
}