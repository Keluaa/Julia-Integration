module ParserScript

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@static if IS_EMBEDDED
    include(p) = Main.include_resource(@__MODULE__, p)
end


using JuliaSyntax
import JuliaSyntax: Kind, ParseStream, GreenNode
import JuliaSyntax: parse!, any_error, build_tree, children, haschildren


struct Parser
    stream::ParseStream
    leaf_cb::Ptr{Cvoid}
    enter_cb::Ptr{Cvoid}
    leave_cb::Ptr{Cvoid}
end


emit_leaf_node(parser::Parser, node) = ccall(parser.leaf_cb, Cvoid, (Cshort,), reinterpret(Int16, kind(node)))
emit_enter_node(parser::Parser, node) = ccall(parser.enter_cb, Cvoid, (Cshort,), reinterpret(Int16, kind(node)))
emit_leave_node(parser::Parser, node) = ccall(parser.leave_cb, Cvoid, ())


function create_parser(
    text::String,
    leaf_cb::Ptr{Cvoid}, enter_cb::Ptr{Cvoid}, leave_cb::Ptr{Cvoid},
    version
)
    version = isnothing(version) ? VERSION : VersionNumber(version)
    index = 1
    stream = ParseStream(text, index; version)
    return Parser(stream, leaf_cb, enter_cb, leave_cb)
end


function run_parser(parser)
    empty!(parser.stream)  # We want the tree to contain only the newly parsed nodes

    parse!(parser.stream, rule=:statement)
    if any_error(parser.stream) || !isempty(parser.stream.diagnostics)
        # TODO: emit diagnostics as user data when building the AST, by using their 'first_byte' and 'last_byte'
    end

    # `build_tree` is postorder, but our callbacks only work in preorder, therefore we must build the tree and then
    # parse it in preorder. Emitting the tree as is it built could have eliminated some allocations.
    tree = build_tree(GreenNode, parser.stream; filename="*current-file*")

    if kind(tree) == K"wrapper"
        for child in children(tree)
            emit_tree(parser, child)
        end
    else
        emit_tree(parser, tree)
    end
end


function emit_tree(parser, node::GreenNode)
    if haschildren(node) && !isempty(children(node))  # TODO: some nodes can `haschildren` but not have any, is this normal?
        emit_enter_node(parser, node)
        for child in children(node)
            emit_tree(parser, child)
        end
        emit_leave_node(parser, node)
    else
        emit_leaf_node(parser, node)
    end
end


end
