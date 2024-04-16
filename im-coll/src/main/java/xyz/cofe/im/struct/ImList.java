package xyz.cofe.im.struct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ImList<E> {
    int size();

    public static <E> ImList<E> empty() {
        //noinspection rawtypes,unchecked
        return new Empty(0);
    }

    public static <E> ImList<E> first(E node) {
        if (node == null) throw new IllegalArgumentException("node==null");
        //noinspection unchecked
        return ((ImList<E>) empty()).prepend(node);
    }

    public static <E> ImList<E> from(Iterable<E> src){
        if( src==null ) throw new IllegalArgumentException("src==null");
        ImList<E> lst = empty();
        for( var e : src ){
            lst = lst.prepend(e);
        }
        return lst.reverse();
    }

    record Empty<E>(int size) implements ImList<E> {}
    record Cons<E>(E elem, ImList<E> next, int size) implements ImList<E> {}

    default ImList<E> prepend(E elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        return new Cons<>(elem, this, size() + 1);
    }

    default Optional<E> get(int index) {
        if (index < 0) return Optional.empty();
        var me = this;
        while (index > 0) {
            if (me instanceof Cons<E> c) {
                me = c.next();
                index--;
            } else {
                return Optional.empty();
            }
        }
        if (me instanceof Cons<E> c) {
            return Optional.of((E) c.elem());
        }

        return Optional.empty();
    }

    default Optional<E> head() {
        if (this instanceof Cons<E> c) return Optional.of((E) c.elem);
        return Optional.empty();
    }

    default ImList<E> tail() {
        if (this instanceof Cons<E> c) return c.next;
        return this;
    }

    default <R> R foldLeft(R init, BiFunction<R, E, R> sum) {
        if (sum == null) throw new IllegalArgumentException("sum==null");
        R value = init;
        var me = this;
        while (true) {
            if (me instanceof ImList.Empty<E>) break;
            if (me instanceof ImList.Cons<E> c) {
                value = sum.apply(value, c.elem());
                me = c.next();
            } else {
                break;
            }
        }
        return value;
    }

    default ImList<E> reverse() {
        return foldLeft(ImList.<E>empty(), ImList::prepend);
    }

    default ImList<E> each(Consumer<E> cons) {
        if (cons == null) throw new IllegalArgumentException("cons==null");
        var me = this;
        while (true) {
            if (me instanceof ImList.Empty<E>) break;
            if (me instanceof ImList.Cons<E> c) {
                cons.accept(c.elem());
                me = c.next();
            } else {
                break;
            }
        }
        return this;
    }

    default <B> ImList<B> flatMap(Function<E, ImList<B>> map) {
        if (map == null) throw new IllegalArgumentException("map==null");

        List<B> lst = new ArrayList<>();

        each(e -> map.apply(e).each(lst::add));

        ImList<B> res = empty();
        for (var i = lst.size() - 1; i >= 0; i--) {
            B e = lst.get(i);
            res = res.prepend(e);
        }
        return res;
    }

    default <B> ImList<B> map(Function<E, B> map) {
        if (map == null) throw new IllegalArgumentException("map==null");

        List<B> lst = new ArrayList<>();

        each(e -> {
            lst.add(map.apply(e));
        });

        ImList<B> res = empty();
        for (var i = lst.size() - 1; i >= 0; i--) {
            B e = lst.get(i);
            res = res.prepend(e);
        }
        return res;
    }

    default ImList<E> filter(Predicate<E> pred) {
        if (pred == null) throw new IllegalArgumentException("pred==null");

        List<E> lst = new ArrayList<>();

        each(e -> {
            if( pred.test(e) )lst.add(e);
        });

        ImList<E> res = empty();
        for (var i = lst.size() - 1; i >= 0; i--) {
            E e = lst.get(i);
            res = res.prepend(e);
        }
        return res;
    }

    default ImList<E> join(ImList<E> other){
        if( other==null ) throw new IllegalArgumentException("other==null");
        return reverse().foldLeft( other, ImList::prepend);
    }
}
