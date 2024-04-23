package xyz.cofe.im.struct;

import java.util.Optional;
import java.util.function.Function;

public sealed interface Either<A, B> {
    record Left<A, B>(A value) implements Either<A, B> {}
    record Right<A, B>(B error) implements Either<A, B> {}

    public static <A, B> Either<A, B> left(A a) {
        return new Left<>(a);
    }

    public static <A, B> Either<A, B> right(B b) {
        return new Right<>(b);
    }

    public default <R> R fold(Function<A, R> leftMap, Function<B, R> rightMap) {
        if (leftMap == null) throw new IllegalArgumentException("leftMap==null");
        if (rightMap == null) throw new IllegalArgumentException("rightMap==null");
        if (this instanceof Either.Left) {
            return leftMap.apply(((Left<A, B>) this).value());
        }
        return rightMap.apply(((Right<A, B>) this).error());
    }

    @SuppressWarnings("unchecked")
    public default <R> Either<R, B> leftMap(Function<A, R> succ) {
        if (succ == null) throw new IllegalArgumentException("succ==null");
        if (this instanceof Either.Right) return (Either<R, B>) this;
        return new Left<>(succ.apply(((Left<A, B>) this).value()));
    }

    public default <R> Either<A, R> rightMap(Function<B, R> map) {
        if (map == null) throw new IllegalArgumentException("map==null");
        if (this instanceof Either.Left<A, B> o) {
            return Either.<A, R>left(o.value);
        }
        var e = ((Right<A, B>) this);
        return Either.<A, R>right(map.apply(e.error()));
    }

    public default <R> Either<A, R> rightFlatMap(Function<B, Either<A,R>> map){
        if( map==null ) throw new IllegalArgumentException("map==null");
        return this.fold(Either::left, map);
    }

    @SuppressWarnings("unchecked")
    public default <R> Either<R, B> leftFlatMap(Function<A, Either<R, B>> succ) {
        if (succ == null) throw new IllegalArgumentException("succ==null");
        if (this instanceof Either.Right) return (Either<R, B>) this;
        return succ.apply(((Left<A, B>) this).value());
    }

    public default boolean isLeft() {return this instanceof Either.Left;}

    public default boolean isRight() {return this instanceof Either.Right;}

    public default Optional<A> toLeftOptional() {return fold(Optional::ofNullable, b -> Optional.empty());}

    public default Optional<B> toRightOptional() {
        return fold(a -> Optional.empty(), Optional::ofNullable);
    }
}
