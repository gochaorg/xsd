package xyz.cofe.im.struct;

import java.util.function.BiConsumer;

public interface Consumer3<A,B,C> {
    void accept(A a, B b, C c);
}
