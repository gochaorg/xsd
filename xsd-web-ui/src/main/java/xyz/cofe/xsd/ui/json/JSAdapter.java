package xyz.cofe.xsd.ui.json;

import xyz.cofe.im.struct.Result;

import static xyz.cofe.im.struct.Result.err;

public sealed interface JSAdapter permits ArrayAdapter,
                                          BooleanAdapter,
                                          NumberAdapter,
                                          ObjectAdapter,
                                          StringAdapter {
    Object jsValue();

    static Result<JSAdapter, String> of(Object obj) {
        if (obj == null) return err("null reference");

        Result<JSAdapter, String> arr = ArrayAdapter.tryParse(obj).map(a -> a);

        return arr
            .errFlatMap(e -> ObjectAdapter.tryParse(obj).map(a -> a))
            .errFlatMap(e -> NumberAdapter.tryParse(obj).map(a -> a))
            .errFlatMap(e -> StringAdapter.tryParse(obj).map(a -> a))
            .errFlatMap(e -> BooleanAdapter.tryParse(obj).map(a -> a))
            ;
    }
}
