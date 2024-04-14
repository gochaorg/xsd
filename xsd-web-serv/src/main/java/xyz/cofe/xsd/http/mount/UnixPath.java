package xyz.cofe.xsd.http.mount;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class UnixPath {
    public static final UnixPath empty;
    static {
        var unixPath = new UnixPath();
        unixPath.pathComponents = List.of();
        unixPath.startWithSlash = false;
        unixPath.endsWithSlash = false;

        empty = unixPath;
    }

    public sealed interface Name {
        String name();

        record Regular(String name) implements Name {}
        record ThisDir(String name) implements Name {}
        record ParentDir(String name) implements Name {}

        static Name of(String name) {
            if( name==null ) throw new IllegalArgumentException("name==null");
            return switch (name) {
                case "." -> new ThisDir(name);
                case ".." -> new ParentDir(name);
                default -> new Regular(name);
            };
        }
    }

    private List<Name> pathComponents;
    private boolean startWithSlash;
    private boolean endsWithSlash;

    public static Optional<UnixPath> parse(String path){
        return parse(path, false);
    }

    public static Optional<UnixPath> parse(String path, boolean allowEmpty){
        if( path==null ) throw new IllegalArgumentException("path==null");
        if( path.isEmpty() ) {
            if( allowEmpty ) {
                return Optional.of( empty );
            }else{
                return Optional.empty();
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

        return Optional.of(unixPath);
    }

    public boolean isEmpty(){
        return !startWithSlash && !endsWithSlash && pathComponents.isEmpty();
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

    private Integer regularCount;
    public int getRegularCount(){
        if( regularCount!=null )return regularCount;
        regularCount = (int)pathComponents.stream().filter(n -> n instanceof Name.Regular).count();
        return regularCount;
    }

    private Integer thisCount;
    public int getThisCount(){
        if( thisCount!=null )return thisCount;
        thisCount = (int)pathComponents.stream().filter(n -> n instanceof Name.ThisDir).count();
        return thisCount;
    }

    private Integer parentCount;
    public int getParentCount(){
        if( parentCount!=null )return parentCount;
        parentCount = (int)pathComponents.stream().filter(n -> n instanceof Name.ParentDir).count();
        return parentCount;
    }

    public boolean isAbsolute(){ return startWithSlash; }
    public boolean isRelative(){ return !startWithSlash; }

    public boolean isEscapeOfRoot(){
        int level = 0;
        Integer minLevel = null;
        for( var p : getPathComponents() ){
            if( p instanceof Name.ParentDir ){
                level--;
            }else if( p instanceof Name.Regular ){
                level++;
            }

            minLevel = minLevel==null ? level : Math.min(minLevel,level);
        }

        return minLevel != null && minLevel < 0;
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

    public String toString(){
        StringBuilder buff = new StringBuilder();
        var i = -1;
        for( var n : getPathComponents() ){
            i++;
            if( i>0 )buff.append("/");
            buff.append(n.name());
        }
        if( startWithSlash )buff.insert(0,"/");
        if( endsWithSlash )buff.append("/");
        return buff.toString();
    }
}
