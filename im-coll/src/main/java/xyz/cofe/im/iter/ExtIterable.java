package xyz.cofe.im.iter;

import xyz.cofe.im.struct.ImList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ExtIterable<E> extends Iterable<E> {
    default <B> ExtIterable<B> flatMap(Function<E, Iterator<B>> map) {
        return () -> {
            return new FMapIterator<E,B>( iterator(), map );
        };
    }

    default <B> ExtIterable<B> map(Function<E,B> map){
        return flatMap( e -> List.of(map.apply(e)).iterator());
    }

    default ExtIterable<E> filter(Predicate<E> pred) {
        return flatMap( e -> {
            if(pred.test(e)){
                return List.of(e).iterator();
            }else{
                return Collections.emptyIterator();
            }
        });
    }

    default List<E> toList(){
        var lst = new ArrayList<E>();
        for( var e : this ){
            lst.add(e);
        }
        return lst;
    }

    default ImList<E> toImList(){
        var lst = ImList.<E>empty();
        for( var e : this ){
            lst = lst.prepend(e);
        }
        return lst.reverse();
    }
}
