package xyz.cofe.xsd.ui.json;

import org.teavm.jso.core.JSBoolean;
import xyz.cofe.coll.im.Result;

import static xyz.cofe.coll.im.Result.error;
import static xyz.cofe.coll.im.Result.ok;

public final class BooleanAdapter implements JSAdapter {
    public final Object jsBool;

    @Override
    public Object jsValue() {
        return jsBool;
    }

    public BooleanAdapter(Object jsBool) {
        if( jsBool ==null ) throw new IllegalArgumentException("jsBool==null");
        this.jsBool = jsBool;
    }

    public static BooleanAdapter of(boolean v){
        return new BooleanAdapter(JSBoolean.valueOf(v));
    }

    public static Result<BooleanAdapter,String> tryParse(Object obj){
        if( obj==null )return error("obj is null");
        return Result.from(TypeOf.of(obj),()->"can't determinate from TypeOf.of")
            .fmap( typeOf ->
                typeOf == TypeOf.BOOLEAN
                    ? ok(new BooleanAdapter(obj))
                    : error("expect boolean")
            );
    }

    public boolean toBoolean(){
        return ((JSBoolean)jsBool).booleanValue();
    }
}
