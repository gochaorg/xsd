package xyz.cofe.nixpath;

import java.util.List;

public sealed interface NixPath permits CanonAbsPath,
                                        UnixPath {
    public List<Name> getPathComponents();
    public boolean isStartWithSlash();
    public boolean isEndsWithSlash();

    public default boolean isAbsolute(){ return isStartWithSlash(); }
    public default boolean isRelative(){ return !isAbsolute(); }

    public default boolean isDirectoryRef(){
        return isEndsWithSlash() || (getPathComponents().isEmpty() && isStartWithSlash());
    }

    public default boolean isEmpty(){
        return !isStartWithSlash() && !isEndsWithSlash() && getPathComponents().isEmpty();
    }

    public default boolean isEscapeOfRoot(){
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

    public static String toString(NixPath path){
        if( path==null ) throw new IllegalArgumentException("path==null");
        StringBuilder buff = new StringBuilder();
        var i = -1;
        for( var n : path.getPathComponents() ){
            i++;
            if( i>0 )buff.append("/");
            buff.append(n.name());
        }
        if( path.isStartWithSlash() )buff.insert(0,"/");
        if( path.isEndsWithSlash() && !path.getPathComponents().isEmpty() )buff.append("/");
        return buff.toString();
    }

}
