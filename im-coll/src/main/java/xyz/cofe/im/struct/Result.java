package xyz.cofe.im.struct;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<A,B> extends Iterable<A> {
    record Ok<A,B>( A value ) implements Result<A,B> {}
    record Err<A,B>( B error ) implements Result<A,B> {}

    public static <A,B> Result<A,B> ok(A a){
        return new Ok<>(a);
    }
    public static <A,B> Result<A,B> err(B b){
        return new Err<>(b);
    }

    public default <R> R fold(Function<A,R> succ, Function<B,R> fail){
        if( succ==null ) throw new IllegalArgumentException("succ==null");
        if( fail==null ) throw new IllegalArgumentException("fail==null");
        if( this instanceof Ok ){
            return succ.apply(((Ok<A,B>)this).value());
        }
        return fail.apply(((Err<A,B>)this).error());
    }

    @SuppressWarnings("unchecked")
    public default <R> Result<R,B> map(Function<A,R> succ ){
        if( succ==null ) throw new IllegalArgumentException("succ==null");
        if( this instanceof Err )return (Result<R, B>) this;
        return new Ok<>(succ.apply(((Ok<A,B>)this).value()));
    }

    public default <R> Result<A,R> mapErr(Function<B,R> map){
        if( map==null ) throw new IllegalArgumentException("map==null");
        if( this instanceof Result.Ok<A,B> o ){
            return Result.<A,R>ok(o.value);
        }
        var e = ((Result.Err<A,B>)this);
        return Result.<A,R>err(map.apply(e.error()));
    }

    @SuppressWarnings("unchecked")
    public default <R> Result<R,B> flatMap(Function<A,Result<R,B>> succ ){
        if( succ==null ) throw new IllegalArgumentException("succ==null");
        if( this instanceof Err )return (Result<R, B>) this;
        return succ.apply( ((Ok<A,B>)this).value() );
    }

    public default boolean isOk(){ return this instanceof Ok; }
    public default boolean isErr(){ return this instanceof Err; }

    public default Optional<A> toOptional(){ return fold(Optional::ofNullable, b -> Optional.empty() ); }
    public default Optional<B> toErrOptional(){
        return fold(a -> Optional.empty(), Optional::ofNullable );
    }

    public default void each(Consumer<A> consumer){
        if( consumer==null ) throw new IllegalArgumentException("consumer==null");
        if( this instanceof Result.Ok<A,B> o ){
            consumer.accept(o.value());
        }
    }

    default Iterator<A> iterator(){
        boolean[] reads = new boolean[]{ false };
        var value = toOptional();
        return new Iterator<A>() {
            @Override
            public boolean hasNext() {
                return !reads[0] && value.isPresent();
            }

            @Override
            public A next() {
                if( !hasNext() )return null;
                reads[0] = true;
                return value.get();
            }
        };
    }
}
