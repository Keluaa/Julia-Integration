module LexerScript

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@static if IS_EMBEDDED
    include(p) = Main.include_resource(@__MODULE__, p)
end


include("utf8_to_utf16.jl")


using JuliaSyntax

import JuliaSyntax: Kind
import JuliaSyntax.Tokenize: startbyte, endbyte

const Lexer = JuliaSyntax.Tokenize.Lexer{IOBuffer}


mutable struct LexerState
    start_byte::Int64
    end_byte::Int64
    token_kind::Int16
end


create_lexer(text::String)::Lexer = JuliaSyntax.Tokenize.Lexer(IOBuffer(text))
start_lexer(lexer::Lexer, state_ptr::Ptr{Cvoid}) = advance_lexer(lexer, Ptr{LexerState}(state_ptr), true)
advance_lexer(lexer::Lexer, state_ptr::Ptr{Cvoid}) = advance_lexer(lexer, Ptr{LexerState}(state_ptr), false)

function advance_lexer(lexer::Lexer, state::Ptr{LexerState}, init::Bool)
    if init
        iter = iterate(lexer)
    else
        iter = iterate(lexer, false)
    end

    iter === nothing && return true
    token, is_done = iter

    # Java uses UTF-16 strings. Those back-and-forth conversions are unfortunate.
    utf16_length = @inbounds count_utf16_codepoints(lexer.io, startbyte(token) + 1, endbyte(token) + 1)

    kind_val = reinterpret(Int16, kind(token))

    # Note: if `state.end_byte` is initialized to 0, then the conversion to Java string indices is direct.
    prev_utf16_end = unsafe_load(Ptr{Int64}(state) + fieldoffset(LexerState, 2))
    utf16_start = prev_utf16_end
    utf16_end = prev_utf16_end + utf16_length

    # `ptr` is not a pointer to a Julia-allocated `LexerState`, but to a struct with the same layout.
    # Furthermore, `unsafe_load` has the disadvantage of making a copy.
    unsafe_store!(Ptr{Int64}(state) + fieldoffset(LexerState, 1), utf16_start)
    unsafe_store!(Ptr{Int64}(state) + fieldoffset(LexerState, 2), utf16_end)
    unsafe_store!(Ptr{Int16}(state) + fieldoffset(LexerState, 3), kind_val)

    return is_done
end

end
