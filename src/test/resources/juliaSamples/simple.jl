# comments are not ignored by JuliaSyntax.jl, unlike with Tokenize.jl

# therefore our parsing is made easier

a = 1 + 1
b=1+1

c = a + b ; d = 45^42

a = 42
a = raw"string"

@test this

# This is a comment

println(a, b, c)

println(println(println(a)), b, c)

#=
Wow!
A
multiline
comment!
=#

begin
    i = check()
    j = @macrotest(i, k)
    # surprise comment
    w
end
