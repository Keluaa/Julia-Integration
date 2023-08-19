module ParserTesting

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@static if IS_EMBEDDED
    include(p) = Main.include_resource(@__MODULE__, p)
end


if IS_EMBEDDED
    ParserScript = Main.ParserScript
    include("utf8_to_utf16.jl")
else
    include("../../../main/resources/julia/parser.jl")
    include("../../../main/resources/julia/utf8_to_utf16.jl")
end


using JuliaSyntax
import JuliaSyntax: Kind, ParseStream
import JuliaSyntax: span, last_byte


struct RefNode
    kind::Int16
    start_offset::Int64  # 0-index
    end_offset::Int64  # 0-index
end


mutable struct TestParser
    text::String
    stream::ParseStream
    flat_tree::Vector{RefNode}
    text_position::Int64  # 1-index
    utf16_text_position::Int64  # 0-index
end


function ParserScript.emit_leaf_node(parser::TestParser, node)
    node_kind = reinterpret(Int16, kind(node))

    start_offset = parser.text_position
    end_offset = start_offset + span(node) - 1

    utf16_span = count_utf16_codepoints(parser.text, start_offset, end_offset)
    start_offset_utf16 = parser.utf16_text_position
    end_offset_utf16 = start_offset_utf16 + utf16_span - 1

    push!(parser.flat_tree, RefNode(node_kind, start_offset_utf16, end_offset_utf16))

    parser.text_position = end_offset + 1
    parser.utf16_text_position = end_offset_utf16 + 1
end


function ParserScript.emit_enter_node(parser::TestParser, node)
    node_kind = reinterpret(Int16, kind(node))

    start_offset = parser.text_position
    end_offset = start_offset + span(node) - 1

    utf16_span = count_utf16_codepoints(parser.text, start_offset, end_offset)
    start_offset_utf16 = parser.utf16_text_position
    end_offset_utf16 = start_offset_utf16 + utf16_span - 1

    push!(parser.flat_tree, RefNode(reinterpret(Int16, kind(node)), start_offset_utf16, end_offset_utf16))
end


ParserScript.emit_leave_node(parser::TestParser, node) = nothing


function parse_all_and_build_tree(text::String, version)
    println("running GC...")
    GC.gc()  # TODO: the GC blocks indefinitely here. It is maybe waiting for an adopted thread?
    println("GC ok")

    real_parser = ParserScript.create_parser(text, C_NULL, C_NULL, C_NULL, version)
    test_parser = TestParser(text, real_parser.stream, Vector{RefNode}(), 1, 1)

    while last_byte(test_parser.stream) + 1 < length(text)
        ParserScript.run_parser(test_parser)
    end

    return test_parser.flat_tree
end

end