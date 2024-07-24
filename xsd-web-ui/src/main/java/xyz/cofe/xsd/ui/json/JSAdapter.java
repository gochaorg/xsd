package xyz.cofe.xsd.ui.json;

import xyz.cofe.coll.im.Result;
import static xyz.cofe.coll.im.Result.error;

public sealed interface JSAdapter permits ArrayAdapter,
                                          BooleanAdapter,
                                          NumberAdapter,
                                          ObjectAdapter,
                                          StringAdapter {
    Object jsValue();

    static Result<JSAdapter, String> of(Object obj) {
        if (obj == null) return error("null reference");

        Result<JSAdapter, String> arr = ArrayAdapter.tryParse(obj).map(a -> a);

        return arr
            .fmapErr(e -> ObjectAdapter.tryParse(obj).map(a -> a))
            .fmapErr(e -> NumberAdapter.tryParse(obj).map(a -> a))
            .fmapErr(e -> StringAdapter.tryParse(obj).map(a -> a))
            .fmapErr(e -> BooleanAdapter.tryParse(obj).map(a -> a))
            ;
    }
}
