module Untokenize

using JuliaSyntax


function token_strings(text::String)::Vector{String}
    tokens = tokenize(text)
    strings = Vector{String}(undef, length(tokens))
    for (i, token) in enumerate(tokens)
        strings[i] = untokenize(token, text)
    end
    return strings
end

end
