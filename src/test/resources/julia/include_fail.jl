module TestScript

const IS_EMBEDDED = isdefined(Main, :IS_EMBEDDED) && Main.IS_EMBEDDED
@assert IS_EMBEDDED

include(p) = Main.include_resource(@__MODULE__, p)

include("file_that_doesnt_exist.jl")

end