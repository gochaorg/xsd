package xyz.cofe.nixpath;


import xyz.cofe.coll.im.Result;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class UnixPath implements NixPath {
    public static final UnixPath empty;
    static {
        var unixPath = new UnixPath();
        unixPath.pathComponents = List.of();
        unixPath.startWithSlash = false;
        unixPath.endsWithSlash = false;

        empty = unixPath;
    }

    private List<Name> pathComponents;
    private boolean startWithSlash;
    private boolean endsWithSlash;

    public static Result<UnixPath,String> parse(String path){
        return parse(path, false);
    }

    public static Result<UnixPath,String> parse(String path, boolean allowEmpty){
        if( path==null ) throw new IllegalArgumentException("path==null");
        if( path.isEmpty() ) {
            if( allowEmpty ) {
                return Result.ok( empty );
            }else{
                return Result.error("empty path not allowed");
            }
        }

        var names = new ArrayList<Name>();

        var startWithSlash = false;
        var endsWithSlash = false;
        var buff = new StringBuilder();
        var state = "start";
        for( var ci=0; ci<path.length(); ci++ ){
            var last = ci==path.length()-1;
            var chr = path.charAt(ci);
            switch (state){
                case "start" -> {
                    switch (chr) {
                        case '/' -> {
                            startWithSlash = true;
                            state = "name";
                            if( last ){
                                endsWithSlash = true;
                            }
                        }
                        default -> {
                            startWithSlash = false;
                            buff.append(chr);
                            state = "name";
                        }
                    }
                }
                case "name" -> {
                    switch (chr) {
                        case '/' -> {
                            if( buff.length()>0 ){
                                names.add(
                                    Name.of(buff.toString())
                                );
                                buff.setLength(0);
                            }
                            if( last ){
                                endsWithSlash = true;
                            }
                        }
                        default -> {
                            buff.append(chr);
                        }
                    }
                }
                default -> {
                }
            }
        }

        if(!buff.isEmpty()){
            names.add(
                Name.of(buff.toString())
            );
            buff.setLength(0);
        }

        var unixPath = new UnixPath();
        unixPath.pathComponents = Collections.unmodifiableList(names);
        unixPath.startWithSlash = startWithSlash;
        unixPath.endsWithSlash = endsWithSlash;

        return Result.ok(unixPath);
    }

    public List<Name> getPathComponents() {
        return pathComponents;
    }
    public boolean isStartWithSlash() {
        return startWithSlash;
    }
    public boolean isEndsWithSlash() {
        return endsWithSlash;
    }

    public String toString(){
        return NixPath.toString(this);
    }

    public Path resolve(Path base){
        if( base==null ) throw new IllegalArgumentException("base==null");
        Path path = base;
        for( var name : getPathComponents() ){
            if( name instanceof Name.Regular r ){
                path = path.resolve(r.name());
            }else if( name instanceof Name.ThisDir r ){
            }else if( name instanceof Name.ParentDir r ){
                var ppath = path.getParent();
                if( ppath!=null ){
                    path = ppath;
                }else{
                    path = path.resolve("..");
                }
            }
        }
        return path;
    }

//    public Result<CanonAbsPath,String> absolute( CanonAbsPath currentDir ){
//        if( currentDir==null ) throw new IllegalArgumentException("currentDir==null");
//        if( isEmpty() )return Result.ok(currentDir);
//
//    }
}
