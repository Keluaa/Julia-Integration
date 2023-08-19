
using StringViews


@inline function count_utf16_codepoints(text)
    utf16_cp::Int64 = 0
    utf8_pos = firstindex(text)
    n = ncodeunits(text)
    @inbounds while utf8_pos <= n
        next_pos = nextind(text, utf8_pos)
        bytes = next_pos - utf8_pos
        utf8_pos = next_pos
        utf16_cp += 1 + (bytes >= 4)
    end
    return utf16_cp
end


@inline function count_utf16_codepoints(io::IOBuffer, utf8_pos::Int, utf8_end::Int)
    io_view = @inbounds StringView(@inbounds @view io.data[utf8_pos:utf8_end])
    return count_utf16_codepoints(io_view)
end


@inline function count_utf16_codepoints(text::String, utf8_pos::Int, utf8_end::Int)
    return count_utf16_codepoints(view(text, utf8_pos:utf8_end))
end
