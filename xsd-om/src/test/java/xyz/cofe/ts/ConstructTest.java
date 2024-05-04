package xyz.cofe.ts;

import java.util.Objects;
import java.util.function.Function;

public class ConstructTest {
    public class Builder<A> {
        public <T> Builder<Function<T,A>> param(T v){
            return null;
        }

        public void cons( A a ){
        }
    }

    void test(){
        var bldr = new Builder<Long>();
        bldr.param(true).param(1).cons( a -> b -> 1L );
    }
}
