package xyz.cofe.im.iter;

import java.util.Iterator;

public interface EachToEnum<A> extends Iterable<A> {
    public record EnumValue<A>( A value, int index ) {}

    public static class EnumerateIterator<A> implements Iterator<EnumValue<A>> {
        private final Iterator<A> iterator;
        private int index;

        public EnumerateIterator(Iterator<A> iterator) {
            if( iterator==null ) throw new IllegalArgumentException("iterator==null");
            this.iterator = iterator;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public EnumValue<A> next() {
            if(iterator.hasNext()){
                A value = iterator.next();
                int idx = index;
                index++;
                return new EnumValue<>(value, idx);
            }
            return null;
        }
    }

    default Iterable<EnumValue<A>> enumerate(){
        var self = this;
        return () -> {
            return new EnumerateIterator<>( self.iterator() );
        };
    }
}
