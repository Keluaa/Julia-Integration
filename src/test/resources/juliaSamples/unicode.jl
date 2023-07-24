# comments are not ignored by JuliaSyntax.jl, unlike with Tokenize.jl

# therefore our parsing is made easier

λ = 1 + 1
b=1+1

c = λ + b ; d = 45^42

a🎷🎷 = 42
aₜ = raw"string"

@🧂 this

# This is a comment

println(λ, b, c)

println(println(println(λ)), b, c)

#=
Wow!
A
multiline
comment!
WITH UNICODE  📡 📡 📡 📡
🎷🎷🎷🎷
=#

begin
    i = check()
    j = @macrotest(i, k)
    # surprise comment
    w
end
