package xyz.cofe.ts;

import xyz.cofe.im.iter.Tree;
import xyz.cofe.im.iter.TreePath;
import xyz.cofe.im.struct.ImList;

import java.util.HashSet;
import java.util.Set;

/**
 * Унаследованный тип
 */
public interface ExtendType extends Type {
    /**
     * Возвращает базовый тип/типы
     * @return базовый тип/типы
     */
    ImList<Type> baseTypes();

    default boolean isAssignableFrom(Type t){
        if( t==null ) throw new IllegalArgumentException("t==null");
        if( t==this )return true;
        if( !(t instanceof ExtendType) ){
            return false;
        }
        ExtendType et = (ExtendType) t;
        var baseType = et.baseTypes();

        Set<Type> visited = new HashSet<>();

        for(TreePath<Type> implType : Tree.root(baseType).pathFollow(p -> this.follow(p, visited)) ){
            if( implType.node() == this ){
                return true;
            }
        }

        return false;
    }

    private ImList<Type> follow(TreePath<Type> tpath, Set<Type> visited){
        if( visited.contains(tpath.node()) )return ImList.empty();
        visited.add(tpath.node());

        var type = tpath.node();
        if( type instanceof ExtendType et ){
            return et.baseTypes();
        }

        return ImList.empty();
    }
}
