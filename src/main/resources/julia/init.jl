
# File loaded in JuliaScript.kt:init before any other script

# Those globals are set before loading this file
# const IS_EMBEDDED = true
# const GET_RESOURCE_FPTR = Ptr{Cvoid}(...)


function read_resource_file(path)
    # Julia scripts are all in the '/julia' resource folder. Java accepts only '/'
    # as separators for resource paths. The callback must handle reading the file
    # since it is stored in the JAR.
    path = normpath("/julia", path)
    @static if Sys.iswindows()
         path = replace(path, '\\' => '/')
    end
    contents_ptr = ccall(GET_RESOURCE_FPTR, Cstring, (Cstring,), path)
    contents = unsafe_string(contents_ptr)
    isempty(contents) && error("Could not get contents of resource file at $path")
    return contents
end


function include_resource(mod::Module, path::String)
    # Allows to replace the include function to be able to load files stored in the JAR
    contents = read_resource_file(path)
    include_string(mod, contents, path)
end


function safe_load_file_string(text::String, filename::String, module_::Module)
    # Wrapper around jl_load_file_string to be able to use jl_call and therefore wrap it in a try catch block
    return ccall(:jl_load_file_string,
        Any, (Cstring, Csize_t, Cstring, Module),
        text, length(text), filename, module_
    )
end
