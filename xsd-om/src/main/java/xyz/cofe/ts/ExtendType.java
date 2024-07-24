package xyz.cofe.ts;

import xyz.cofe.coll.im.iter.Tree;
import xyz.cofe.coll.im.iter.TreePath;
import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;

import java.util.HashMap;
import java.util.Map;

import static xyz.cofe.coll.im.Result.ok;

/**
 * Унаследованный тип
 */
public sealed interface ExtendType extends Type permits Struct {
    /**
     * Возвращает базовый тип/типы
     * @return базовый тип/типы
     */
    ImList<Result<Type,String>> baseTypes();

    @Override
    default Result<Boolean,String> isAssignableFrom(Type type){
        if( type ==null ) throw new IllegalArgumentException("t==null");
        if( type ==this )return ok(true);
        if( !(type instanceof ExtendType) ){
            return ok(false);
        }

        var root = new ExtendVisitNode(ok(type));
        var tPathIter = Tree.root(root).pathFollow( tpath -> {
            if( hasCycle(tpath))return ImList.of();
            return follow(tpath.node());
        });

        for( var tpNode : tPathIter ){
            Result<Type,String> tRes = tpNode.node().type();
            if( tRes.toOptional().map( t -> t==this ).orElse(false) ){
                return ok(true);
            }
        }

        return ok(false);
    }

    private boolean hasCycle(TreePath<ExtendVisitNode> path){
        Map<Type,Integer> cntr = new HashMap<>();
        for( var r : path.reversePath() ){
            r.type().each( t -> {
                cntr.put( t, cntr.getOrDefault(t,0)+1 );
            });
        }
        return cntr.values().stream().anyMatch(n -> n > 1);
    }

    private ImList<ExtendVisitNode> follow(ExtendVisitNode type){
        if( type.type().isOk() ){
            //noinspection OptionalGetWithoutIsPresent
            var typ = type.type().toOptional().get();
            if( typ instanceof ExtendType et ){
                return et.baseTypes().map(ExtendVisitNode::new);
            }
        }
        return ImList.of();
    }
}
