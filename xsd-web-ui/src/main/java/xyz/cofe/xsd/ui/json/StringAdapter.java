package xyz.cofe.xsd.ui.json;

import org.teavm.jso.core.JSString;
import xyz.cofe.coll.im.Result;

import static xyz.cofe.coll.im.Result.error;
import static xyz.cofe.coll.im.Result.ok;

public final class StringAdapter implements JSAdapter {
    public final Object jsString;

    @Override
    public Object jsValue() {
        return jsString;
    }

    public StringAdapter(Object jsString) {
        if( jsString==null ) throw new IllegalArgumentException("jsString==null");
        this.jsString = jsString;
    }

    public static StringAdapter of(String value){
        if( value==null ) throw new IllegalArgumentException("value==null");
        return new StringAdapter(JSString.valueOf(value));
    }

    public static Result<StringAdapter,String> tryParse(Object obj){
        if( obj==null )return error("obj is null");
        return Result.from(TypeOf.of(obj),()->"can't determinate from TypeOf.of")
            .fmap( typeOf ->
                typeOf == TypeOf.STRING
                    ? ok(new StringAdapter(obj))
                    : error("expect string")
            );
    }

    public String toString(){
        return ((JSString)jsString).stringValue();
    }

    public int length(){
        return ((JSString)jsString).getLength();
    }
}
