module ParserScript

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@static if IS_EMBEDDED
    include(p) = Main.include_resource(@__MODULE__, p)
end


include("utf8_to_utf16.jl")


using JuliaSyntax

import JuliaSyntax: Kind




end
