package xyz.cofe.ecoll;

public sealed interface EvListEvent<A> {
    record Inserted<A>(int index, A item) implements EvListEvent<A> {}
    record Updated<A>(int index, A item, A old) implements EvListEvent<A> {}
    record Deleted<A>(int index, A old) implements EvListEvent<A> {}
    record FullyChanged<A>() implements EvListEvent<A> {}
}
