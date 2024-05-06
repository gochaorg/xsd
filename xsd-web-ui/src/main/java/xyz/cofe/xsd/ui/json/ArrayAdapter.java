package xyz.cofe.xsd.ui.json;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import xyz.cofe.im.iter.ExtIterable;
import xyz.cofe.im.struct.Result;

import java.util.Iterator;
import java.util.Optional;

import static xyz.cofe.im.struct.Result.err;
import static xyz.cofe.im.struct.Result.ok;

public final class ArrayAdapter implements JSAdapter,
                                           ExtIterable<JSAdapter> {
    public final Object jsArray;

    @Override
    public Object jsValue() {
        return jsArray;
    }

    public ArrayAdapter(Object jsArray) {
        this.jsArray = jsArray;
    }

    public static Result<ArrayAdapter,String> tryParse(Object obj){
        if( obj==null )return err("obj is null");
        return Result.of(TypeOf.of(obj),"can't determinate from TypeOf.of")
            .flatMap( typeOf ->
                typeOf == TypeOf.OBJECT && ArrayAdapter.isArray(obj)
                    ? ok(new ArrayAdapter(obj))
                    : err("expect object and not array")
            );
    }

    @JSBody(params = {"obj"}, script = "return Array.isArray(obj);")
    public static native boolean isArray(Object obj);

    public int size(){
        return ((JSArray<?>)jsArray).getLength();
    }

    @SuppressWarnings("rawtypes")
    public Result<JSAdapter,String> get(int index){
        if( index<0 )return err("index < 0");
        if( index>=size() )return err("index >= size");

        var v = ((JSArray)jsArray).get(index);
        var tOpt = TypeOf.of(v);
        if( tOpt.isEmpty() )return err("can't determinate");

        var t = tOpt.get();
        if( t==TypeOf.UNDEF )return err("undefined");

        return JSAdapter.of(v);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void add(JSAdapter value){
        if( value==null ) throw new IllegalArgumentException("value==null");

        ((JSArray)jsArray).push(value.jsValue());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Result<JSAdapter,String> set(int index, JSAdapter value){
        if( value==null ) throw new IllegalArgumentException("value==null");

        var previous = get(index);

        ((JSArray)jsArray).set(index, value);
        return previous;
    }

    @SuppressWarnings("rawtypes")
    public Result<JSAdapter,String> delete(int index){
        var previous = get(index);

        ((JSArray)jsArray).splice(index,1);

        return previous;
    }

    @JSBody(params = {"obj"}, script = "return obj.slice();")
    private static native Object cloneArr(Object obj);

    @Override
    public Iterator<JSAdapter> iterator() {
        var jsArr =
            //this;
            new ArrayAdapter(cloneArr(jsArray));
//            new ArrayAdapter( ((JSArray)jsArray).sli );

        int size = size();
        int[] ptr = new int[]{ 0 };

        return new Iterator<JSAdapter>() {
            @Override
            public boolean hasNext() {
                return ptr[0] < size;
            }

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            @Override
            public JSAdapter next() {
                var v = jsArr.get(ptr[0]);
                ptr[0]++;
                return v.toOptional().get();
            }
        };
    }
}
