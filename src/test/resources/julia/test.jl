module TestScript

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@assert IS_EMBEDDED

include(p) = Main.include_resource(@__MODULE__, p)

include("include_test.jl")

function some_function(i::Cint)::Cint
    return i + 1
end

const FPTR_some_function = @cfunction(some_function, Cint, (Cint,))

end