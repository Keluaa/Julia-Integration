package com.github.keluaa.juliaintegration.lexer

class UnknownKindException : Exception {
    constructor(name: String, isRange: Boolean) :
            super(if (isRange) "Unknown kind range: BEGIN_$name" else "Unknown kind: $name")
    constructor(name: String): this(name, false)
}