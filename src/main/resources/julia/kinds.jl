module KindsInterface

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@static if IS_EMBEDDED
    include(p) = Main.include_resource(@__MODULE__, p)
end


using JuliaSyntax
import JuliaSyntax: Token, Kind, is_error
import JuliaSyntax.Tokenize: Lexer


const KindInt = Int16


@assert sizeof(Kind) == sizeof(KindInt)
@assert reinterpret(KindInt, typemax(Kind)) < typemax(KindInt)
@assert 2*sizeof(KindInt) == sizeof(Tuple{KindInt, KindInt}) == sizeof(UInt32)
@assert JuliaSyntax._token_error_descriptions isa Dict{Kind, String}


kind_count() = length(instances(Kind))


function kind_from_name(name::String)::KindInt
    try
        return reinterpret(KindInt, convert(Kind, name))
    catch
        return -1
    end
end

kind_from_name(name::Cstring)::Cint = kind_from_name(unsafe_string(name))


"""
    kind_range_from_name(name)

The range from `BEGIN_\$name` to `END_\$name` (inclusive) in the `Kind` enum.
"""
function kind_range_from_name(name::String)::Tuple{KindInt, KindInt}
    try
        first = reinterpret(KindInt, convert(Kind, "BEGIN_" * name))
        last = reinterpret(KindInt, convert(Kind, "END_" * name))
        return (first, last)
    catch
        return (-1, -1)
    end
end

function kind_range_from_name(name::Cstring)::Culonglong
    range = name |> unsafe_string |> kind_range_from_name
    packed = UInt32(reinterpret(unsigned(KindInt), first(range)))
    packed |= UInt32(reinterpret(unsigned(KindInt), last(range))) << (8*sizeof(KindInt))
    return packed
end


function error_string(kind::Kind)::String
    if is_error(kind)
        return get(JuliaSyntax._token_error_descriptions, kind) do
            # Default error message
            JuliaSyntax._token_error_descriptions[K"error"]
        end
    else
        return ""
    end
end

error_string(kind_val::KindInt)::Cstring = kind_val |> Kind |> error_string |> pointer


const FPTR_kind_count = @cfunction(kind_count, Clonglong, ())
const FPTR_kind_from_name = @cfunction(kind_from_name, Cint, (Cstring,))
const FPTR_kind_range_from_name = @cfunction(kind_range_from_name, Culonglong, (Cstring,))
const FPTR_error_string = @cfunction(error_string, Cstring, (KindInt,))

end
