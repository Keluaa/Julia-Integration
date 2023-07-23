package com.github.keluaa.juliaintegration.lexer

import com.intellij.psi.tree.TokenSet
import com.github.keluaa.juliaintegration.lexer.JuliaKinds.*

object KindSets {
    val KEYWORDS = TokenSet.create(
            baremodule(), begin(), `break`(), `const`(), `continue`(), `do`(),
            export(), `for`(), function(), global(), `if`(), `import`(),
            let(), local(), macro(), module(), quote(), `return`(),
            struct(), `try`(), using(), `while`(), catch(), finally(),
            `else`(), elseif(), end()
    )

    val CONTEXTUAL_KEYWORDS = TokenSet.create(
            abstract(), `as`(), doc(), mutable(), outer(), primitive(),
            type(), `var`()
    )

    val ALL_KEYWORDS = TokenSet.orSet(KEYWORDS, CONTEXTUAL_KEYWORDS)

    val LITERALS = TokenSet.create(
            Integer(), BinInt(), HexInt(), OctInt(), Float(), Float32(),
            String_(), Char(), CmdString(), `true`(), `false`(),
    )

    val OPERATORS = TokenSet.create(
            ErrorInvalidOperator(), ErrorStarInsteadOfCap(),
            splat(), ASSIGNMENT(), PairArrow(), CONDITIONAL(), ARROW(), LAZYOR(),
            LAZYAND(), COMPARISON(), LEFT_PIPE(), PIPE(), COLON(), PLUS(),
            TIMES(), RATIONAL(), BITSHIFT(), POWER(), DECL(), where(),
            DOT(), ExclamationMark(), Quote(), DotQuote(), Lambda(), UNICODE_OP()
    )

    val PARSER = TokenSet.create(TOMBSTONE(), MACRO_NAME())

    val SYNTAX = TokenSet.create(
            block(), call(), dotcall(), comparison(), curly(), inert(),
            juxtapose(), string(), cmdstring(), char(), macrocall(), parameters(),
            toplevel(), tuple(), ref(), vect(), parens(), importpath(),
            braces(), bracescat(), hcat(), vcat(), ncat(), typed_hcat(),
            typed_vcat(), typed_ncat(), row(), nrow(), generator(), filter(),
            cartesian_iterator(), comprehension(), typed_comprehension(), wrapper()
    )

    val COMMENTS = TokenSet.create(Comment())
    val STRINGS = TokenSet.create(String_(), CmdString())
}