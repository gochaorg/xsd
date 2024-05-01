package xyz.cofe.im.struct;

import xyz.cofe.im.iter.EachToEnum;
import xyz.cofe.im.iter.EachToMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ImList<E> extends Iterable<E>,
                                   EachToMap<E> {
    int size();

    default boolean isEmpty() {return size() == 0;}

    default boolean isNonEmpty() {return size() > 0;}

    public static <E> ImList<E> empty() {
        //noinspection rawtypes,unchecked
        return new Empty(0);
    }

    public static <E> ImList<E> first(E node) {
        if (node == null) throw new IllegalArgumentException("node==null");
        //noinspection unchecked
        return ((ImList<E>) empty()).prepend(node);
    }

    public static <E> ImList<E> from(Iterable<E> src) {
        if (src == null) throw new IllegalArgumentException("src==null");
        ImList<E> lst = empty();
        for (var e : src) {
            lst = lst.prepend(e);
        }
        return lst.reverse();
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    public static <E> ImList<E> from(Optional<E> src) {
        if (src == null) throw new IllegalArgumentException("src==null");
        return src.isPresent() ? ImList.first(src.get()) : empty();
    }

    record Empty<E>(int size) implements ImList<E> {}
    record Cons<E>(E elem, ImList<E> next, int size) implements ImList<E> {}

    @SuppressWarnings("unchecked")
    default Iterator<E> iterator() {
        ImList<E>[] ptr = new ImList[]{this};
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return ptr[0] instanceof ImList.Cons<E>;
            }

            @Override
            public E next() {
                if (ptr[0] instanceof ImList.Cons<E> cons) {
                    var res = cons.elem();
                    ptr[0] = cons.next();
                    return res;
                }
                return null;
            }
        };
    }

    default ImList<E> prepend(E elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        return new Cons<>(elem, this, size() + 1);
    }

    default ImList<E> prepend(Iterable<E> elems){
        if( elems instanceof ImList<E> l ){
            return l.join(this);
        }
        return ImList.from(elems).join(this);
    }

    default ImList<E> append(E elem) {
        var lst = toList();
        lst.add(elem);
        return from(lst);
    }

    default ImList<E> append(Iterable<E> elems){
        if( elems instanceof ImList<E> l ){
            return join(l);
        }
        return join(ImList.from(elems));
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

    default <R> R foldRight(R init, BiFunction<R, E, R> sum) {
        if (sum == null) throw new IllegalArgumentException("sum==null");
        R value = init;
        var lst = toList();
        for (var i = lst.size() - 1; i >= 0; i--) {
            value = sum.apply(value, lst.get(i));
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
            if (pred.test(e)) lst.add(e);
        });

        ImList<E> res = empty();
        for (var i = lst.size() - 1; i >= 0; i--) {
            E e = lst.get(i);
            res = res.prepend(e);
        }
        return res;
    }

    default ImList<E> join(ImList<E> other) {
        if (other == null) throw new IllegalArgumentException("other==null");
        return reverse().foldLeft(other, ImList::prepend);
    }

    default List<E> toList() {
        var lst = new ArrayList<E>();
        each(lst::add);
        return lst;
    }

    default ImList<EachToEnum.EnumValue<E>> enumerate() {
        int[] idx = new int[]{0};
        var lst = map(e -> {
            var i = idx[0];
            idx[0]++;
            return new EachToEnum.EnumValue<>(e, i);
        });
        return lst;
    }

    record ZipEntry<A,B>( A left, B right ) {}

    default <B> ImList<ZipEntry<E,B>> zip(Iterable<B> iter){
        if( iter==null ) throw new IllegalArgumentException("iter==null");
        var it1 = iterator();
        var it2 = iter.iterator();
        var res = ImList.<ZipEntry<E,B>>empty();
        while (true){
            if( !it1.hasNext() )break;
            if( !it2.hasNext() )break;
            var a = it1.next();
            var b = it2.next();
            res = res.prepend(new ZipEntry<>(a,b));
        }
        return res.reverse();
    }
}
