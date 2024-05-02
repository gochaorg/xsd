package xyz.cofe.im.struct;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<A, B> extends Iterable<A> {
    public static final class NoValue {
        private NoValue(){}
        public final static NoValue instance = new NoValue();

        @Override
        public String toString(){
            return "NoValue";
        }
    }

    record Ok<A, B>(A value) implements Result<A, B> {}
    record Err<A, B>(B error) implements Result<A, B> {}

    public static <A, B> Result<A, B> ok(A a) {
        return new Ok<>(a);
    }

    public static <B> Result<NoValue, B> ok() {
        return new Ok<>(NoValue.instance);
    }

    public static <A, B> Result<A, B> err(B b) {
        return new Err<>(b);
    }

    @SuppressWarnings({"OptionalAssignedToNull", "OptionalUsedAsFieldOrParameterType"})
    public static <A, B> Result<A, B> of(Optional<A> aOpt, B err){
        if( aOpt==null ) throw new IllegalArgumentException("aOpt==null");
        return aOpt.<Result<A, B>>map(Result::ok).orElseGet(() -> err(err));
    }

    public default <R> R fold(Function<A, R> succ, Function<B, R> fail) {
        if (succ == null) throw new IllegalArgumentException("succ==null");
        if (fail == null) throw new IllegalArgumentException("fail==null");
        if (this instanceof Ok) {
            return succ.apply(((Ok<A, B>) this).value());
        }
        return fail.apply(((Err<A, B>) this).error());
    }

    @SuppressWarnings("unchecked")
    public default <R> Result<R, B> map(Function<A, R> succ) {
        if (succ == null) throw new IllegalArgumentException("succ==null");
        if (this instanceof Err) return (Result<R, B>) this;
        return new Ok<>(succ.apply(((Ok<A, B>) this).value()));
    }

    public default <R> Result<A, R> errMap(Function<B, R> map) {
        if (map == null) throw new IllegalArgumentException("map==null");
        if (this instanceof Result.Ok<A, B> o) {
            return Result.<A, R>ok(o.value);
        }
        var e = ((Result.Err<A, B>) this);
        return Result.<A, R>err(map.apply(e.error()));
    }

    public default <R> Result<A, R> errFlatMap(Function<B, Result<A,R>> map){
        if( map==null ) throw new IllegalArgumentException("map==null");
        return this.fold(Result::ok, map);
    }

    @SuppressWarnings("unchecked")
    public default <R> Result<R, B> flatMap(Function<A, Result<R, B>> succ) {
        if (succ == null) throw new IllegalArgumentException("succ==null");
        if (this instanceof Err) return (Result<R, B>) this;
        return succ.apply(((Ok<A, B>) this).value());
    }

    public default boolean isOk() {return this instanceof Ok;}

    public default boolean isErr() {return this instanceof Err;}

    public default Optional<A> toOptional() {return fold(Optional::ofNullable, b -> Optional.empty());}

    public default ImList<A> toImList(){
        var opt = toOptional();
        if( opt.isEmpty() )return ImList.empty();
        return ImList.first(opt.get());
    }

    public default Optional<B> toErrOptional() {
        return fold(a -> Optional.empty(), Optional::ofNullable);
    }

    public default void each(Consumer<A> consumer) {
        if (consumer == null) throw new IllegalArgumentException("consumer==null");
        if (this instanceof Result.Ok<A, B> o) {
            consumer.accept(o.value());
        }
    }

    default Iterator<A> iterator() {
        boolean[] reads = new boolean[]{false};
        var value = toOptional();
        return new Iterator<A>() {
            @Override
            public boolean hasNext() {
                return !reads[0] && value.isPresent();
            }

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            @Override
            public A next() {
                if (!hasNext()) return null;
                reads[0] = true;
                return value.get();
            }
        };
    }
}
