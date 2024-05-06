package xyz.cofe.xsd.ui.json;

import org.teavm.jso.JSBody;
import org.teavm.jso.core.JSNumber;
import xyz.cofe.im.struct.Result;

import static xyz.cofe.im.struct.Result.err;
import static xyz.cofe.im.struct.Result.ok;

public final class NumberAdapter implements JSAdapter {
    public final Object jsNum;

    @Override
    public Object jsValue() {
        return jsNum;
    }

    public NumberAdapter(Object jsNum) {
        if( jsNum==null ) throw new IllegalArgumentException("jsNum==null");
        this.jsNum = jsNum;
    }

    public static NumberAdapter of(int value){
        return new NumberAdapter(JSNumber.valueOf(value));
    }

    public static NumberAdapter of(double value){
        return new NumberAdapter(JSNumber.valueOf(value));
    }

    public static Result<NumberAdapter,String> tryParse(Object obj){
        if( obj==null )return err("obj is null");
        return Result.of(TypeOf.of(obj),"can't determinate from TypeOf.of")
            .flatMap( typeOf ->
                typeOf == TypeOf.NUMBER
                    ? ok(new NumberAdapter(obj))
                    : err("expect number")
            );
    }

    @JSBody(params = {"n"}, script = "return Number(n) === n && n % 1 === 0;")
    private static native boolean is_int(Object n);

    @JSBody(params = {"n"}, script = "return Number(n) === n && n % 1 !== 0")
    private static native boolean is_float(Object n);

    public int toInt(){
        return ((JSNumber)jsNum).intValue();
    }

    public double toDouble(){
        return ((JSNumber)jsNum).doubleValue();
    }
}
