package xyz.cofe.im.struct;

public record Tuple2<A,B>( A a, B b ) {
    public static <A,B> Tuple2<A,B> of( A a, B b ){
        return new Tuple2<>(a,b);
    }
}
