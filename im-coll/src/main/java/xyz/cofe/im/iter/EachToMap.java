package xyz.cofe.im.iter;

import xyz.cofe.im.struct.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EachToMap<A> extends Iterable<A> {
    default <K,V> Map<K, V> toMap(Function<A, Tuple2<K, V>> map, BiFunction<V, V, V> merge, Supplier<Map<K,V>> newMap) {
        if( map==null ) throw new IllegalArgumentException("map==null");
        if( merge==null ) throw new IllegalArgumentException("merge==null");
        if( newMap==null ) throw new IllegalArgumentException("newMap==null");
        var m = newMap.get();

        for( var el : this ){
            var tup = map.apply(el);
            var k = tup.a();
            var v = tup.b();
            if( m.containsKey(k) ){
                m.put(k, merge.apply(m.get(k), v));
            }else{
                m.put(k,v);
            }
        }
        return m;
    }

    default <K,V> Map<K,V> toMap(Function<A, Tuple2<K, V>> map, BiFunction<V, V, V> merge){
        if( map==null ) throw new IllegalArgumentException("map==null");
        if( merge==null ) throw new IllegalArgumentException("merge==null");
        return toMap(map, merge, HashMap::new);
    }

    default <K,V> Map<K,V> toMap(Function<A, Tuple2<K, V>> map) {
        if( map==null ) throw new IllegalArgumentException("map==null");
        return toMap(map, (old,cur)->cur, HashMap::new);
    }
}
