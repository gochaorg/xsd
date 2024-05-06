package xyz.cofe.xsd.ui.json;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSString;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;

import static xyz.cofe.im.struct.Result.err;
import static xyz.cofe.im.struct.Result.ok;

public final class ObjectAdapter implements JSAdapter {
    public final Object jsObject;

    @Override
    public Object jsValue() {
        return jsObject;
    }

    public ObjectAdapter(Object jsObject) {
        if( jsObject==null ) throw new IllegalArgumentException("jsObject==null");
        this.jsObject = jsObject;
    }

    public ObjectAdapter(){
        this.jsObject = newObj();
    }

    public static ObjectAdapter create(){
        return new ObjectAdapter(newObj());
    }

    public static Result<ObjectAdapter,String> tryParse(Object obj){
        if( obj==null )return err("obj is null");
        return Result.of(TypeOf.of(obj),"can't determinate from TypeOf.of")
            .flatMap( typeOf ->
                typeOf == TypeOf.OBJECT && !ArrayAdapter.isArray(obj)
                ? ok(new ObjectAdapter(obj))
                : err("expect object and not array")
            );
    }

    @JSBody(params = {}, script = "return {};")
    private static native JSObject newObj();

    @JSBody(params = {"obj", "key"}, script = "return obj[key];")
    private static native JSObject getValue(Object obj, JSString key);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    private static native void setValue(Object obj, JSString key, Object value);

    @JSBody(params = {"obj", "key"}, script = "delete obj[key];")
    private static native void deleteValue(Object obj, JSString key);

    @JSBody(params = {"obj"}, script = "return keys(obj);")
    private static native JSArray<JSString> keys(Object obj);

    public ImList<String> getKeys(){
        var keys = keys(jsObject);
        var res = ImList.<String>empty();
        for( var i=0; i<keys.getLength(); i++ ){
            var k = keys.get(i);
            res = res.prepend(k.stringValue());
        }
        return res.reverse();
    }

    public Result<JSAdapter,String> get(String key){
        if( key==null ) throw new IllegalArgumentException("key==null");

        var v = getValue(jsObject, JSString.valueOf(key));
        if( v==null )return err("null reference");

        var tOpt = TypeOf.of(v);
        if( tOpt.isEmpty() )return err("can't determinate");

        var t = tOpt.get();
        if( t==TypeOf.UNDEF )return err("undefined");

        return JSAdapter.of(v);
    }

    public Result<String,String> getString(String key){
        return get(key)
            .flatMap(n -> n instanceof StringAdapter s ? Result.ok(s) : Result.err("not str"))
            .map(StringAdapter::toString);
    }

    public Result<Boolean,String> getBoolean(String key){
        return get(key)
            .flatMap(n -> n instanceof BooleanAdapter s ? Result.ok(s) : Result.err("not boolean"))
            .map(BooleanAdapter::toBoolean);
    }

    public void put(String key, JSAdapter value){
        if( key==null ) throw new IllegalArgumentException("key==null");
        if( value==null ) throw new IllegalArgumentException("value==null");

        setValue(jsObject, JSString.valueOf(key), value.jsValue() );
    }

    public void delete(String key){
        if( key==null ) throw new IllegalArgumentException("key==null");

        deleteValue(jsObject, JSString.valueOf(key));
    }
}
