package com.github.keluaa.juliaintegration.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Enum of [JuliaKind] covering all `JuliaSyntax.Kind` values.
 *
 * Naming prioritizes the original Kind name, and falls back to a description of it.
 * Ranges are in all caps, with the trailing 'S' removed.
 */
@Suppress("unused", "EnumEntryName")
enum class JuliaKinds(name: String, isRange: Boolean = false, elType: IElementType? = null) {
    None("None"),
    EndMarker("EndMarker"),
    Comment("Comment"),
    Whitespace("Whitespace", elType = TokenType.WHITE_SPACE),
    NewlineWs("NewlineWs"),
    Identifier("Identifier"),
    `@`("@"),
    `,`(","),
    Semicolon(";"),

    // Tokenization errors
    ERROR("ERRORS", true),

    // Keywords
    baremodule("baremodule"),
    begin("begin"),
    `break`("break"),
    `const`("const"),
    `continue`("continue"),
    `do`("do"),
    export("export"),
    `for`("for"),
    function("function"),
    global("global"),
    `if`("if"),
    `import`("import"),
    let("let"),
    local("local"),
    macro("macro"),
    module("module"),
    quote("quote"),
    `return`("return"),
    struct("struct"),
    `try`("try"),
    using("using"),
    `while`("while"),
    catch("catch"),
    finally("finally"),
    `else`("else"),
    elseif("elseif"),
    end("end"),
    `abstract`("abstract"),
    `as`("as"),
    doc("doc"),
    mutable("mutable"),
    outer("outer"),
    primitive("primitive"),
    type("type"),
    `var`("var"),

    // Literals
    Integer("Integer"),
    BinInt("BinInt"),
    HexInt("HexInt"),
    OctInt("OctInt"),
    Float("Float"),
    Float32("Float32"),
    String_("String"),  // `String` causes a namespace clash, hence the underscore
    Char("Char"),
    CmdString("CmdString"),
    `true`("true"),
    `false`("false"),

    DELIMITER("DELIMITERS", true),

    // Operators
    ErrorInvalidOperator("ErrorInvalidOperator"),
    ErrorStarInsteadOfCap("Error**"),

    splat("..."),

    ASSIGNMENT("ASSIGNMENTS", true),
    PairArrow("=>"),
    CONDITIONAL("?"),
    ARROW("ARROW", true),
    LAZYOR("||"),
    LAZYAND("&&"),
    COMPARISON("COMPARISON", true),

    LEFT_PIPE("<|"),
    PIPE("|>"),

    COLON("COLON", true),
    PLUS("PLUS", true),

    TIMES("TIMES", true),

    RATIONAL("//"),
    BITSHIFT("BITSHIFTS", true),

    POWER("POWER", true),

    DECL("::"),

    where("where"),

    DOT("."),

    ExclamationMark("!"),
    Quote("'"),
    DotQuote(".'"),
    Lambda("->"),

    UNICODE_OP("UNICODE_OPS", true),

    // PARSER_TOKENS
    TOMBSTONE("TOMBSTONE"),
    MACRO_NAME("MACRO_NAMES", true),

    // SYNTAX_KINDS
    block("block"),
    call("call"),
    dotcall("dotcall"),
    comparison("comparison"),
    curly("curly"),
    inert("inert"),  // QuoteNode; not quasiquote
    juxtapose("juxtapose"),  // Numeric juxtaposition like 2x
    string("string"),  // A string interior node (possibly containing interpolations)
    cmdstring("cmdstring"),  // A cmd string node (containing delimiters plus string)
    char("char"),  // A char string node (containing delims + char data)
    macrocall("macrocall"),
    parameters("parameters"),  // the list after ; in f(; a=1)
    toplevel("toplevel"),
    tuple("tuple"),
    ref("ref"),
    vect("vect"),
    parens("parens"),
    importpath("importpath"),
    // Concatenation syntax
    braces("braces"),
    bracescat("bracescat"),
    hcat("hcat"),
    vcat("vcat"),
    ncat("ncat"),
    typed_hcat("typed_hcat"),
    typed_vcat("typed_vcat"),
    typed_ncat("typed_ncat"),
    row("row"),
    nrow("nrow"),
    // Comprehensions
    generator("generator"),
    filter("filter"),
    cartesian_iterator("cartesian_iterator"),
    comprehension("comprehension"),
    typed_comprehension("typed_comprehension"),
    // Container for a single statement/atom plus any trivia and errors
    wrapper("wrapper");

    val kind = JuliaKind(name, isRange)
    val elementType = elType

    operator fun invoke() = elementType ?: kind

    companion object {
        /**
         * Map of all enum values in `JuliaSyntax.Kind` (excluding ranges delimiters) to their
         * equivalent [IElementType].
         */
        internal val kinds: Array<IElementType>

        init {
            val ourKinds = values()
            val totalKindCount = ourKinds.last().kind.kind.toInt() + 1
            val kindsIt = ourKinds.iterator()
            var currentKind = kindsIt.next()
            var currentKindRange = currentKind.kind.range
            kinds = Array(totalKindCount) { i ->
                while (i !in currentKindRange) {
                    // This loop allows to define both a JuliaKind range and some specific kinds
                    // part of this range. For this to work properly, the range must be defined
                    // first, and the tokens immediately after.
                    currentKind = kindsIt.next()
                    currentKindRange = currentKind.kind.range
                }
                return@Array currentKind()
            }
        }

        fun fromIdx(kindIdx: Short) = kinds[kindIdx.toInt()]
    }
}