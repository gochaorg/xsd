package xyz.cofe.im.iter;

import java.util.Iterator;
import java.util.function.Function;

public class FMapIterator<A, B> implements Iterator<B> {
    private final Iterator<A> source;
    private Iterator<B> current;
    private final Function<A, Iterator<B>> map;

    public FMapIterator(Iterator<A> source, Function<A, Iterator<B>> map) {
        this.source = source;
        this.map = map;

        if (source.hasNext()) {
            current = map.apply(source.next());
        }
    }

    @Override
    public boolean hasNext() {
        while (current != null) {
            if (current.hasNext()) return true;
            if (source.hasNext()) {
                current = map.apply(source.next());
            } else {
                current = null;
                break;
            }
        }
        return false;
    }

    @Override
    public B next() {
        while (current != null) {
            if (current.hasNext()) return current.next();
            if (source.hasNext()) {
                current = map.apply(source.next());
            } else {
                break;
            }
        }
        return null;
    }
}
